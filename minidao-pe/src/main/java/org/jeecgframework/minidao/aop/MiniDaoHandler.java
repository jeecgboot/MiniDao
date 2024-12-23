package org.jeecgframework.minidao.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.IgnoreSaas;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.annotation.id.IdType;
import org.jeecgframework.minidao.annotation.id.TableId;
import org.jeecgframework.minidao.aspect.EmptyInterceptor;
import org.jeecgframework.minidao.def.MiniDaoConstants;
import org.jeecgframework.minidao.pagehelper.dialect.PageAutoDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnMapRowMapper;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnOriginalMapRowMapper;
import org.jeecgframework.minidao.util.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;

/**
 * 
 * @Title: MiniDao动态代理类
 * @author 张代浩
 * @mail jeecgos@163.com
 * @category www.jeecg.com
 * @date 20130817
 * @version V1.0
 */
@SuppressWarnings("rawtypes")
public class MiniDaoHandler implements InvocationHandler {
	private static final Log logger = LogFactory.getLog(MiniDaoHandler.class);
	
	/**自定义的数据源*/
    @Autowired(required = false)
    @Qualifier("minidaoDataSource")
    public DataSource dataSource;
    
	@Autowired
	@Lazy
	private JdbcTemplate jdbcTemplate;
	@Autowired
	@Lazy
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/** minidao拦截器 */
	@Autowired(required = false)
	@Lazy
	private EmptyInterceptor emptyInterceptor;

	private ApplicationContext applicationContext;
	/**
	 * 多数据源配置情况下，minidao默认的数据源名
	 */
	private String MUTL_DATASOURCES_MINIDAO_DF_DSNAME = "minidaoDataSource";
	private String UPPER_KEY = "upper";

	private String LOWER_KEY = "lower";
	/**
	 * map的关键字类型 三个值
	 */
	private String keyType = "origin";
	private boolean formatSql = false;
	private boolean showSql = false;

	//自定获取方言
	protected PageAutoDialect pageAutoDialect = new PageAutoDialect();
     //序列查询sql
	public static final String SEQ_NEXTVAL_SQL = "SELECT %s.nextval FROM DUAL";

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 返回结果
		Object returnObj = null;
		// SQL模板
		String templateSql = null;
		// SQL模板参数
		Map<String, Object> sqlParamsMap = new HashMap<String, Object>();
		// 分页参数
		MiniDaoPage pageSetting = new MiniDaoPage();

		// Step.0 判断是否是Hiber实体维护方法，如果是执行Hibernate方式实体维护
//		Map<String, Object> rs = new HashMap<String, Object>();

		// Step.1装载SQL模板，所需参数
		templateSql = installDaoMetaData(pageSetting, method, sqlParamsMap, args);

		// Step.3解析SQL模板，返回可执行SQL
		String executeSql = parseSqlTemplate(method, templateSql, sqlParamsMap);
		//jeecg : Minidao报错“Template java/lang/Object_toString.sql not found”的解决方案
		if(executeSql==null || "".equals(executeSql)){
			return null;
		}

		// Step.4 组装SQL占位符参数
		Map<String, Object> sqlMap = installPlaceholderSqlParam(executeSql, sqlParamsMap);

		//step.5  检查Dialect初始化
		initCheckDialectExists();

		// Step.6 获取SQL执行返回值
		try {
			returnObj = getReturnMinidaoResult(pageSetting, method, executeSql, sqlMap, args);
		} catch (Exception e) {
			returnObj = null;
			if(e instanceof EmptyResultDataAccessException){
				//数据查询为空，不抛出Spring异常
			}else{
				e.printStackTrace();
				throw e;
			}
		}
		if (showSql) {
			logger.info("Print MiniDao-Original-SQL :\n\n" + executeSql);
		}
		return returnObj;
	}

	/**
	 * 判斷是否是執行的方法（非查詢）
	 * 
	 * @param methodName
	 * @return
	 */
	private static boolean checkActiveKey(String methodName) {
		String keys[] = MiniDaoConstants.INF_METHOD_ACTIVE.split(",");
		for (String s : keys) {
			if (methodName.startsWith(s))
				return true;
		}
		return false;
	}
	
	/**
	 * 判斷SQL是否（非查詢）
	 * 
	 * @param methodName
	 * @return
	 */
	private static boolean checkActiveSql(String sql) {
		sql = sql.trim().toLowerCase();
		String keys[] = MiniDaoConstants.INF_METHOD_ACTIVE.split(",");
		for (String s : keys) {
			if (sql.startsWith(s))
				return true;
		}
		return false;
	}

	/**
	 * 判斷是否批處理
	 * 
	 * @param methodName
	 * @return
	 */
	private static boolean checkBatchKey(String methodName) {
		String keys[] = MiniDaoConstants.INF_METHOD_BATCH.split(",");
		for (String s : keys) {
			if (methodName.startsWith(s))
				return true;
		}
		return false;
	}

	/**
	 * 把批量处理的结果拼接起来
	 * 
	 * @Author JueYue
	 * @date 2013-11-17
	 */
	private void addResulArray(int[] result, int index, int[] arr) {
		int length = arr.length;
		for (int i = 0; i < length; i++) {
			result[index - length + i] = arr[i];
		}
	}

	/**
	 * 批处理
	 * 
	 * @Author JueYue
	 * @date 2013-11-17
	 * @return
	 */
	private int[] batchUpdate(String executeSql) {
		String[] sqls = executeSql.split(";");
		if (sqls.length < 100) {
			return jdbcTemplate.batchUpdate(sqls);
		}
		int[] result = new int[sqls.length];
		List<String> sqlList = new ArrayList<String>();
		for (int i = 0; i < sqls.length; i++) {
			sqlList.add(sqls[i]);
			if (i % 100 == 0) {
				addResulArray(result, i + 1, jdbcTemplate.batchUpdate(sqlList.toArray(new String[0])));
				sqlList.clear();
			}
		}
		addResulArray(result, sqls.length, jdbcTemplate.batchUpdate(sqlList.toArray(new String[0])));
		return result;
	}

	/**
	 * 根据参数设置map的key大小写
	 **/
	private RowMapper<Map<String, Object>> getColumnMapRowMapper() {
		if (getKeyType().equalsIgnoreCase(LOWER_KEY)) {
			return new MiniColumnMapRowMapper();
		} else if (getKeyType().equalsIgnoreCase(UPPER_KEY)) {
			return new ColumnMapRowMapper();
		} else {
			return new MiniColumnOriginalMapRowMapper();
		}
	}

//	/**
//	 * 获取总数sql - 如果要支持其他数据库，修改这里就可以
//	 *
//	 * @param sql
//	 * @return
//	 */
//	private String getCountSql(String sql) {
//		//update-begin---author:scott----date:20170803------for:分页count去掉排序，兼容SqlServer，同时提高效率--------
//		sql = removeOrderBy(sql);
//		//update-end---author:scott----date:20170803------for:分页count去掉排序，兼容SqlServer，同时提高效率--------
//		return "select count(0) from (" + sql + ") tmp_count";
//	}
//
//	/**
//	 * 为了兼容SQLServer
//	 * 去除子查询中的order by (也为了提升分页性能)
//	 * @param sql
//	 * @return
//	 */
//	public String removeOrderBy(String sql) {
//		if(sql==null){
//			return null;
//		}
//		//sql = sql.replaceAll("(?i)order by [\\s|\\S]+$", "");
//		try {
//			logger.debug(" --- 去除子查询中的order by (为了兼容SQLServer) --- orig sql="+sql);
//			sql = SqlServerParse.class.newInstance().removeOrderBy(sql);
//			logger.debug(" --- 去除子查询中的order by (为了兼容SQLServer) --- upda sql="+sql);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sql;
//	}

	public JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			try {
				namedParameterJdbcTemplate = applicationContext.getBean(NamedParameterJdbcTemplate.class);
				jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
			} catch (BeansException e) {
				logger.warn(e.getMessage());
				Map<String, DataSource> multDataSourceMap = applicationContext.getBeansOfType(DataSource.class);
				if (multDataSourceMap != null) {
					String keyOfTheFirst = multDataSourceMap.entrySet().stream().filter(d -> d.getKey().equals(MUTL_DATASOURCES_MINIDAO_DF_DSNAME)).findFirst().get().getKey();
					namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(multDataSourceMap.get(keyOfTheFirst));
					jdbcTemplate = new JdbcTemplate(multDataSourceMap.get(keyOfTheFirst));
				}
			}
		}else{
		    //update-begin--Author:wangshuai--Date:20211018--for: 用户自定义的数据源是否为空，不为空则走用户自定义的数据源
            if(null != dataSource){
                namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
                jdbcTemplate = new JdbcTemplate(dataSource);  
            }
            //update-end--Author:wangshuai--Date:20211018--for: 用户自定义的数据源是否为空，不为空则走用户自定义的数据源
        }
		
		return jdbcTemplate;
	}

	public String getKeyType() {
		return keyType;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	/**
	 * 获取MiniDao处理结果集
	 * 
	 * @param dbType
	 * @param pageSetting
	 * @param jdbcTemplate
	 * @param method
	 * @param executeSql
	 * @return 结果集
	 */
	@SuppressWarnings("unchecked")
	private Object getReturnMinidaoResult(MiniDaoPage pageSetting, Method method, String executeSql, Map<String, Object> paramMap, Object[] args) {
		//update-begin---author:scott----date:20210608------for:springboot2.5/多数据配置问题, 导致jdbcTemplate、namedParameterJdbcTemplate注入是个null.注入失败.-------
		if (namedParameterJdbcTemplate == null) {
			try {
				namedParameterJdbcTemplate = applicationContext.getBean(NamedParameterJdbcTemplate.class);
				jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
			} catch (BeansException e) {
				logger.warn(e.getMessage());
				Map<String, DataSource> multDataSourceMap = applicationContext.getBeansOfType(DataSource.class);
				if (multDataSourceMap != null) {
					String keyOfTheFirst = multDataSourceMap.entrySet().stream().filter(d -> d.getKey().equals(MUTL_DATASOURCES_MINIDAO_DF_DSNAME)).findFirst().get().getKey();
					namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(multDataSourceMap.get(keyOfTheFirst));
					jdbcTemplate = new JdbcTemplate(multDataSourceMap.get(keyOfTheFirst));
				}
			}
		}else{
            //update-begin--Author:wangshuai--Date:20211018--for: 用户自定义的数据源是否为空，不为空则走用户自定义的数据源
            if(null!=dataSource){
                namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
                jdbcTemplate = new JdbcTemplate(dataSource);
            }
            //update-end--Author:wangshuai--Date:20211018--for: 用户自定义的数据源是否为空，不为空则走用户自定义的数据源
        }
		
		if(emptyInterceptor==null){
			try {
				emptyInterceptor = applicationContext.getBean(EmptyInterceptor.class);
			} catch (BeansException e) {
				logger.warn(e.getMessage());
			}
		}
		//update-end---author:scott----date:20210608------for:springboot2.5/多数据配置问题, 导致jdbcTemplate、namedParameterJdbcTemplate注入是个null.注入失败.-------

		// step.4.调用SpringJdbc引擎，执行SQL返回值
		// 5.1获取返回值类型[Map/Object/List<Object>/List<Map>/基本类型]
		String methodName = method.getName();
		// 判斷是否非查詢方法
		if (checkActiveKey(methodName) || checkActiveSql(executeSql)) {
			//update-begin---author:scott----date:20210726------for:主键策略填值ID逻辑-------
			//主键ID策略填值逻辑
			List<Field> idFdList = null;
			Object obj = null;
			if(methodName.startsWith("insert") && args!=null){
				obj = args[0];
				Field[] fields = obj.getClass().getDeclaredFields();
				idFdList = Arrays.asList(fields).stream().filter(a -> (a.getAnnotation(TableId.class) != null && a.getAnnotation(TableId.class).type() == IdType.AUTO)).collect(Collectors.toList());
			}
			//update-end---author:scott----date:20210726------for:主键策略填值ID逻辑--------

			//update-begin---author:scott----date:20180104------for:支持ID自增策略生成并返回主键ID--------
			if (idFdList!=null && idFdList.size()>0) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				if (paramMap != null) {
					MapSqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
					namedParameterJdbcTemplate.update(executeSql, paramSource,keyHolder,new String[]{idFdList.get(0).getName()});
				} else {
					jdbcTemplate.update(executeSql,keyHolder);
				}

				//设置ID自增返回值
				Map idVal = new HashMap();
				idVal.put(idFdList.get(0).getName(),keyHolder.getKey().intValue());
				try {
					ReflectUtil.setIdFieldValue(idVal, obj);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}else{
				if (paramMap != null) {
					return namedParameterJdbcTemplate.update(executeSql, paramMap);
				} else {
					return jdbcTemplate.update(executeSql);
				}
			}
		} else if (checkBatchKey(methodName)) {
			return batchUpdate(executeSql);
		} else {
			// 如果是查詢操作
			Class<?> returnType = method.getReturnType();
			if (returnType.isPrimitive()) {
				//update-begin---author:scott----date:20160906------for:修复非包装类型，无法传参数问题--------
				Number number = namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, BigDecimal.class);
					//jdbcTemplate.queryForObject(executeSql, BigDecimal.class);
				//update-begin---author:scott----date:20160906------for:修复非包装类型，无法传参数问题--------
				
				if ("int".equals(returnType.getCanonicalName())) {
					return number.intValue();
				} else if ("long".equals(returnType.getCanonicalName())) {
					return number.longValue();
				} else if ("double".equals(returnType.getCanonicalName())) {
					return number.doubleValue();
				}
			} else if (returnType.isAssignableFrom(List.class) || returnType.isAssignableFrom(MiniDaoPage.class)) {
				int page = pageSetting.getPage();
				int rows = pageSetting.getRows();
				if (page != 0 && rows != 0) {
					if (returnType.isAssignableFrom(MiniDaoPage.class)) {
						if (paramMap != null) {
							String countsql = MiniDaoUtil.getCountSql(executeSql);
							if (showSql) {
								logger.info("page smart countsql===> "+ countsql);
								logger.info("page smart params===> "+ paramMap);
							}
							pageSetting.setTotal(namedParameterJdbcTemplate.queryForObject(countsql, paramMap, Integer.class));
						} else {
							String countsql = MiniDaoUtil.getCountSql(executeSql);
							if (showSql) {
								logger.info("page countsql===> "+countsql);
							}
							pageSetting.setTotal(jdbcTemplate.queryForObject(countsql, Integer.class));
						}
					}
					//判断方言，获取分页sql
					if (pageAutoDialect.getDelegate()!=null) {
						 executeSql = pageAutoDialect.getDelegate().getPageSql(executeSql,pageSetting);
						if (showSql) {
							logger.info("page executeSql===> "+executeSql);
						}
					}
					//executeSql = MiniDaoUtil.createPageSql(dbType, executeSql, page, rows);
				}
				
				//update-begin---author:scott----date:20180705------for: 返回List<基础类型>，返回值为空问题处理--------
				RowMapper resultType = getListRealType(method);
				Class resultClassType = getListClassType(method);
				List list;
				if (paramMap != null) {
					if (showSql) {
						logger.info("page executeSql params===> "+paramMap);
					}
					if (resultClassType.isAssignableFrom(String.class) || resultClassType.isAssignableFrom(Date.class) || resultClassType.isAssignableFrom(Integer.class) || resultClassType.isAssignableFrom(Double.class) || resultClassType.isAssignableFrom(Long.class)) {
						list = namedParameterJdbcTemplate.queryForList(executeSql, paramMap, resultClassType);
					} else {
						list = namedParameterJdbcTemplate.query(executeSql, paramMap, resultType);
					}
				} else {
					if (resultClassType.isAssignableFrom(String.class) || resultClassType.isAssignableFrom(Date.class) || resultClassType.isAssignableFrom(Integer.class) || resultClassType.isAssignableFrom(Double.class) || resultClassType.isAssignableFrom(Long.class)) {
						list = jdbcTemplate.queryForList(executeSql, resultClassType);
					} else {
						list = jdbcTemplate.query(executeSql, resultType);
					}
				}
				//update-end---author:scott----date:20180705------for: 返回List<基础类型>，返回值为空问题处理--------
				
				if (returnType.isAssignableFrom(MiniDaoPage.class)) {
					pageSetting.setResults(list);
					return pageSetting;
				} else {
					return list;
				}
			} else if (returnType.isAssignableFrom(Map.class)) {
				// Map类型
				if (paramMap != null) {
					return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, getColumnMapRowMapper());
				} else {
					return jdbcTemplate.queryForObject(executeSql, getColumnMapRowMapper());
				}
			} else if (returnType.isAssignableFrom(String.class)) {
				if (paramMap != null) {
					return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, String.class);
				} else {
					return jdbcTemplate.queryForObject(executeSql, String.class);
				}
			} else if (MiniDaoUtil.isWrapClass(returnType)) {
				if (paramMap != null) {
					return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, returnType);
				} else {
					return jdbcTemplate.queryForObject(executeSql, returnType);
				}
			} else {
				//---update-begin--author:scott---date:20160909----for:支持spring4---------
				// 对象类型
				RowMapper<?> rm = BeanPropertyRowMapper.newInstance(returnType);
				//RowMapper<?> rm = ParameterizedBeanPropertyRowMapper.newInstance(returnType);
				//---update-end--author:scott---date:20160909----for:支持spring4---------
				if (paramMap != null) {
					return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, rm);
				} else {
					return jdbcTemplate.queryForObject(executeSql, rm);
				}
			}
		}
		return null;
	}

	/**
	 * 获取真正的类型
	 * 
	 * @param genericReturnType
	 * @param RowMapper
	 * @return
	 */
	private RowMapper<?> getListRealType(Method method) {
		ResultType resultType = method.getAnnotation(ResultType.class);
		if (resultType != null) {
			if (resultType.value().equals(Map.class)) {
				return getColumnMapRowMapper();
			}
			//---update-begin--author:scott---date:20160909----for:支持spring4---------
			return BeanPropertyRowMapper.newInstance(resultType.value());
			//---update-end--author:scott---date:20160909----for:支持spring4---------
		}
		String genericReturnType = method.getGenericReturnType().toString();
		String realType = genericReturnType.replace("java.util.List", "").replace("<", "").replace(">", "");
		if (realType.contains("java.util.Map")) {
			return getColumnMapRowMapper();
		} else if (realType.length() > 0) {
			try {
				//---update-begin--author:scott---date:20160909----for:支持spring4---------
				return BeanPropertyRowMapper.newInstance(Class.forName(realType));
				//---update-end--author:scott---date:20160909----for:支持spring4---------
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e.fillInStackTrace());
				throw new RuntimeException("minidao get class error ,class name is:" + realType);
			}
		}
		return getColumnMapRowMapper();
	}
	
	/**
	 * 获取真正的类型
	 * 
	 * @param genericReturnType
	 * @param rowMapper
	 * @return
	 */
	private Class getListClassType(Method method) {
		ResultType resultType = method.getAnnotation(ResultType.class);
		if (resultType != null) {
			if (resultType.value().equals(Map.class)) {
				return Map.class;
			}
			return resultType.value();
		}
		String genericReturnType = method.getGenericReturnType().toString();
		String realType = genericReturnType.replace("java.util.List", "").replace("<", "").replace(">", "");
		if (realType.contains("java.util.Map")) {
			return Map.class;
		} else if (realType.length() > 0) {
			try {
				return Class.forName(realType);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e.fillInStackTrace());
				throw new RuntimeException("minidao get class error ,class name is:" + realType);
			}
		}
		return null;
	}

	/**
	 * 装载SQL模板参数
	 * 
	 * @param pageSetting
	 * 
	 * @param method
	 * @param sqlParamsMap
	 *            返回(装载模板参数)
	 * @param args
	 * @return templateSql(@SQL标签的SQL)
	 * @throws Exception
	 */
	private String installDaoMetaData(MiniDaoPage pageSetting, Method method, Map<String, Object> sqlParamsMap, Object[] args) throws Exception {
		if(emptyInterceptor==null){
			try {
				emptyInterceptor = applicationContext.getBean(EmptyInterceptor.class);
			} catch (BeansException e) {
				logger.warn(e.getMessage());
			}
		}
		
		//update-begin---author:scott----date:20160511------for:minidao拦截器逻辑--------
		//System.out.println(" -- methodName -- "+ methodName );
		if(emptyInterceptor!=null && args!= null && args.length==1){
			String methodName = method.getName();
			Object obj = args[0];
			if(obj!=null){
				Field[] fields = obj.getClass().getDeclaredFields();
				if(methodName.startsWith("insert")){
					if(emptyInterceptor!=null){
						emptyInterceptor.onInsert(fields, obj);
					}
				}
				if(methodName.startsWith("update")){
					if(emptyInterceptor!=null){
						emptyInterceptor.onUpdate(fields, obj);
					}
				}
				//saas 查询拦截器
				IgnoreSaas ignoreSaas = method.getAnnotation(IgnoreSaas.class);
				if (ignoreSaas == null) {
					if(methodName.startsWith("select") || methodName.startsWith("query") || methodName.startsWith("get") || methodName.startsWith("page")){
						if(emptyInterceptor!=null){
							emptyInterceptor.onSelect(fields, obj);
						}
					}
				}
				//reflect(obj);
			}
		}else{
			//saas 查询拦截器
			String methodName = method.getName();
			IgnoreSaas ignoreSaas = method.getAnnotation(IgnoreSaas.class);
			if (ignoreSaas == null) {
				if (args != null && args.length > 0) {
					Object obj = args[0];
					if (obj != null) {
						Field[] fields = obj.getClass().getDeclaredFields();
						if (methodName.startsWith("select") || methodName.startsWith("query") || methodName.startsWith("get") || methodName.startsWith("page")) {
							if (emptyInterceptor != null) {
								emptyInterceptor.onSelect(fields, obj);
							}
						}
					}
				}
			}
		}
		//update-begin---author:scott----date:20160511------for:minidao拦截器逻辑--------


		//update-begin---author:scott----date:20210726------for:主键策略填值ID逻辑-------
		//主键策略填值ID逻辑
		String methodName = method.getName();
		if(methodName.startsWith("insert") && args!=null){
			Object obj = args[0];
			Field[] fields = obj.getClass().getDeclaredFields();
			this.initIdAnnotation(fields, obj);
		}
		//update-end---author:scott----date:20210726------for:主键策略填值ID逻辑--------

		String templateSql = null;
		// 如果方法参数大于1个的话，方法必须使用注释标签Arguments
		boolean arguments_flag = method.isAnnotationPresent(Arguments.class);
		if (arguments_flag) {
			// [1].获取方法的参数标签
			Arguments arguments = method.getAnnotation(Arguments.class);
			logger.debug("@Arguments------------------------------------------" + Arrays.toString(arguments.value()));
			if (arguments.value().length != args.length) {
				// 校验机制-如果注释标签参数数目大于方法的参数，则抛出异常
				throw new Exception("注释标签@Arguments参数数目，与方法参数数目不相等~");
			}
			// step.2.将args转换成键值对，封装成Map对象
			int args_num = 0;
			for (String v : arguments.value()) {
				// update-begin--Author:fancq Date:20140102 for：支持多数据分页
				if (v.equalsIgnoreCase("page")) {
					pageSetting.setPage(Integer.parseInt(args[args_num].toString()));
				}
				if (v.equalsIgnoreCase("rows")) {
					pageSetting.setRows(Integer.parseInt(args[args_num].toString()));
				}
				// update-end--Author:fancq Date:20140102 for：支持多数据分页
				sqlParamsMap.put(v, args[args_num]);
				args_num++;
			}
		} else {
			// 如果未使用[参数标签]
			if (args != null && args.length >= 1) {
				//---update-begin----author:scott-----date:20160302-----for:支持新参数注解写法--------------
				String[] params = ParameterNameUtils.getMethodParameterNamesByAnnotation(method);
				if(params==null || params.length==0){
					throw new Exception("方法参数数目>=2，必须使用：方法标签@Arguments 或  参数标签@param");
				}
				if (params.length != args.length) {
					throw new Exception("方法参数数目>=2，参数必须使用：标签@param");
				}
				int args_num = 0;
				for (String v : params) {
					if(v==null){
						throw new Exception("Dao接口定义，所有参数必须使用@param标签~");
					}
					if (v.equalsIgnoreCase("page")) {
						pageSetting.setPage(Integer.parseInt(args[args_num].toString()));
					}
					if (v.equalsIgnoreCase("rows")) {
						pageSetting.setRows(Integer.parseInt(args[args_num].toString()));
					}
					sqlParamsMap.put(v, args[args_num]);
					args_num++;
				}
				//---update-end----author:scott-----date:20160302-----for:支持新参数注解写法--------------
			} else if (args != null && args.length == 1) {
				// step.2.将args转换成键值对，封装成Map对象
				sqlParamsMap.put(MiniDaoConstants.SQL_FTL_DTO, args[0]);
			}

		}

		// [2].获取方法的SQL标签
		if (method.isAnnotationPresent(Sql.class)) {
			Sql sql = method.getAnnotation(Sql.class);
			// 如果用户采用自定义标签SQL，则SQL文件无效
			if (StringUtils.isNotEmpty(sql.value())) {
				templateSql = sql.value();
			}
			logger.debug("@Sql------------------------------------------" + sql.value());
		}
		return templateSql;
	}

	/**
	 * 字段注解初始化数值
	 * @param fields
	 * @param obj
	 * @throws Exception
	 */
	private void initIdAnnotation(Field[] fields, Object obj) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int j = 0; j < fields.length; j++) {
			//fields[j].setAccessible(true);
			String fieldName = fields[j].getName();
			//1.获取所有的注解
			TableId annotation = fields[j].getAnnotation(TableId.class);
			//2.根据枚举类判断类型
			if (annotation != null) {
				IdType type = annotation.type();
				Object value = null;
				for (IdType idType : IdType.values()) {
					//3.设置字段数值
					if (type == IdType.AUTO) {
						//数据库自增,忽略该字段
						break;
					} else if (type == IdType.ID_WORKER) {
						//分布式ID
						value = SnowflakeIdWorker.generateId();
						map.put(fieldName, value);
						break;
					} else if (type == IdType.UUID) {
						//UUID
						value = UUID.randomUUID().toString().replace("-", "");
						map.put(fieldName, value);
						break;
					} else if (type == IdType.ID_SEQ) {
						//1.获取查询序列名称
						String seqName = annotation.seqName();
						if (StringUtils.isBlank(seqName)) {
							throw new Exception("ID_SEQ注解定义，参数必须配置序列名");
						}
						//2.执行查询序列sql，获取返回值
						value = jdbcTemplate.queryForObject(String.format(SEQ_NEXTVAL_SQL, seqName), Object.class);
						map.put(fieldName, value);
						break;
					}
				}
				//3.设置数值
				try {
					ReflectUtil.setIdFieldValue(map, obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 组装占位符参数 -> Map
	 * 
	 * @param executeSql
	 * @return
	 * @throws OgnlException
	 */
	private Map<String, Object> installPlaceholderSqlParam(String executeSql, Map sqlParamsMap) throws OgnlException {
		Map<String, Object> map = new HashMap<String, Object>();
		//update-begin---author:scott----date:20160906------for:参数不支持下划线解决--------
		//update-begin---author:chenrui ---date:20231226  for：[jimu/issue/#2269]参数表达式新增中文支持------------
		String regEx = ":[ tnx0Bfr]*[0-9a-z.A-Z_\\u4e00-\\u9fa5]+"; // 表示以：开头，[0-9或者.或者A-Z大小写或者中文汉字]的任意字符，超过一个
		//update-end---author:chenrui ---date:20231226  for：[jimu/issue/#2269]参数表达式新增中文支持------------
		//update-begin---author:scott----date:20160906------for:参数不支持下划线解决--------
		Pattern pat = Pattern.compile(regEx);
		Matcher m = pat.matcher(executeSql);
		while (m.find()) {
			logger.debug(" Match [" + m.group() + "] at positions " + m.start() + "-" + (m.end() - 1));
			String ognl_key = m.group().replace(":", "").trim();
			logger.debug(" --- minidao --- 解析参数 --- " + ognl_key);
			map.put(ognl_key, Ognl.getValue(ognl_key, sqlParamsMap));
		}
		return map;
	}

	public boolean isFormatSql() {
		return formatSql;
	}

	/**
	 * 解析SQL模板
	 * 
	 * @param method
	 * @param templateSql
	 * @param sqlParamsMap
	 * @return 可执行SQL
	 * @throws Exception 
	 */
	private String parseSqlTemplate(Method method, String templateSql, Map<String, Object> sqlParamsMap) throws Exception {
		// step.1.根据命名规范[接口名_方法名.sql]，获取SQL模板文件的路径
		String executeSql = null;

		// step.2.获取SQL模板内容
		// step.3.通过模板引擎给SQL模板装载参数,解析生成可执行SQL
		if (StringUtils.isNotEmpty(templateSql)) {
			executeSql = FreemarkerParseFactory.parseTemplateContent(templateSql, sqlParamsMap);
		} else {
			String sqlTempletPath = method.getDeclaringClass().getName().replace(".", "/").replace("/dao/", "/sql/") + "_" + method.getName() + ".sql";
			if (!FreemarkerParseFactory.isExistTemplate(sqlTempletPath)) {
				sqlTempletPath = method.getDeclaringClass().getName().replace(".", "/") + "_" + method.getName() + ".sql";
			}
			logger.debug("MiniDao-SQL-Path:" + sqlTempletPath);
			executeSql = FreemarkerParseFactory.parseTemplate(sqlTempletPath, sqlParamsMap);
		}
		return executeSql;
	}
	
	//update-begin--Author:luobaoli  Date:20150710 for：增加存储过程入参解析方法
	/**
	 * 将解析参数的代码单独抽取出来
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public List<Object> procedureParamsList(Method method,Object[] args) throws Exception{
		List<Object> procedureParamsList = new ArrayList<Object>();
		//如果方法参数大于1个的话，方法必须使用注释标签Arguments
		boolean arguments_flag = method.isAnnotationPresent(Arguments.class);
		if(arguments_flag){
			//[1].获取方法的参数标签
			Arguments arguments = method.getAnnotation(Arguments.class);  
            logger.debug("@Arguments------------------------------------------"+Arrays.toString(arguments.value()));
            if(arguments.value().length > args.length){
            	//校验机制-如果注释标签参数数目大于方法的参数，则抛出异常
            	throw new Exception("[注释标签]参数数目，不能大于[方法参数]参数数目");
            }
            // step.2.将args转换成键值对，封装成Map对象
            for(int i=0;i<arguments.value().length;i++){
            	procedureParamsList.add(args[i]);
            }
		}else{
			//System.out.println(StringUtils.join(args));
			procedureParamsList = Arrays.asList(args);
		}
		return procedureParamsList;
	}
	//update-begin--Author:luobaoli  Date:20150710 for：增加存储过程入参解析方法


	/**
	 *  检查初始化Dialect
	 */
	private void initCheckDialectExists() {
		DataSource dataSource = getJdbcTemplate().getDataSource();
		pageAutoDialect.initDelegateDialect(dataSource);
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	
	public EmptyInterceptor getEmptyInterceptor() {
		return emptyInterceptor;
	}

	public void setEmptyInterceptor(EmptyInterceptor emptyInterceptor) {
		this.emptyInterceptor = emptyInterceptor;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
