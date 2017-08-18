package org.jeecgframework.minidao.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.aspect.EmptyInterceptor;
import org.jeecgframework.minidao.def.MiniDaoConstants;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnMapRowMapper;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnOriginalMapRowMapper;
import org.jeecgframework.minidao.util.FreemarkerParseFactory;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.jeecgframework.minidao.util.ParameterNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 
 * @Title:MiniDaoHandler
 * @description:MiniDAO 拦截器
 * @author 张代浩
 * @mail zhangdaiscott@163.com
 * @category www.jeecg.org
 * @date 20130817
 * @version V1.0
 */
@SuppressWarnings("rawtypes")
public class MiniDaoHandler implements InvocationHandler {

	private static final Logger logger = Logger.getLogger(MiniDaoHandler.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private String UPPER_KEY = "upper";

	private String LOWER_KEY = "lower";
	/**
	 * map的关键字类型 三个值
	 */
	private String keyType = "origin";
	private boolean formatSql = false;

	private boolean showSql = false;

	private String dbType;
	/**
	 * minidao拦截器
	 */
	private EmptyInterceptor emptyInterceptor;

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

		// Step.4 组装SQL占位符参数
		Map<String, Object> sqlMap = installPlaceholderSqlParam(executeSql, sqlParamsMap);

		// Step.5 获取SQL执行返回值
		try {
			returnObj = getReturnMinidaoResult(dbType, pageSetting, method, executeSql, sqlMap);
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
			System.out.println("MiniDao-SQL:\n\n" + executeSql);
			logger.info("MiniDao-SQL:\n\n" + executeSql);
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

	/**
	 * 获取总数sql - 如果要支持其他数据库，修改这里就可以
	 * 
	 * @param sql
	 * @return
	 */
	private String getCountSql(String sql) {
		//update-begin---author:scott----date:20170803------for:分页count去掉排序，兼容SqlServer，同时提高效率--------
		sql = removeOrderBy(sql);
		//update-end---author:scott----date:20170803------for:分页count去掉排序，兼容SqlServer，同时提高效率--------
		return "select count(0) from (" + sql + ") tmp_count";
	}

	/**
	 * 去除子查询中的order by (特别是SQLServer)
	 * @param sql
	 * @return
	 */
	public String removeOrderBy(String sql) {
		if(sql==null){
			return null;
		}
		sql = sql.replaceAll("(?i)order by [\\s|\\S]+$", "");  
	   return sql;  
	}  
	 
	public String getDbType() {
		return dbType;
	}

	public JdbcTemplate getJdbcTemplate() {
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
	private Object getReturnMinidaoResult(String dbType, MiniDaoPage pageSetting, Method method, String executeSql, Map<String, Object> paramMap) {
		// step.4.调用SpringJdbc引擎，执行SQL返回值
		// 5.1获取返回值类型[Map/Object/List<Object>/List<Map>/基本类型]
		String methodName = method.getName();
		//update-begin---author:scott----date:20160906------for:增加通过sql判断是否非查询操作--------
		// 判斷是否非查詢方法
		if (checkActiveKey(methodName) || checkActiveSql(executeSql)) {
		//update-end---author:scott----date:20160906------for:增加通过sql判断是否非查询操作--------
			if (paramMap != null) {
				return namedParameterJdbcTemplate.update(executeSql, paramMap);
			} else {
				return jdbcTemplate.update(executeSql);
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
							pageSetting.setTotal(namedParameterJdbcTemplate.queryForObject(getCountSql(executeSql), paramMap, Integer.class));
						} else {
							pageSetting.setTotal(jdbcTemplate.queryForObject(getCountSql(executeSql), Integer.class));
						}
					}
					executeSql = MiniDaoUtil.createPageSql(dbType, executeSql, page, rows);
				}

				RowMapper resultType = getListRealType(method);
				List list;
				if (paramMap != null) {
					list = namedParameterJdbcTemplate.query(executeSql, paramMap, resultType);
				} else {
					list = jdbcTemplate.query(executeSql, resultType);
				}
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
	 * @param rowMapper
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
		//update-begin---author:scott----date:20160511------for:minidao拦截器逻辑--------
		//System.out.println(" -- methodName -- "+ methodName );
		if(emptyInterceptor!=null && args!= null && args.length==1){
			String methodName = method.getName();
			Object obj = args[0];
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
			//reflect(obj);
		}
		//update-begin---author:scott----date:20160511------for:minidao拦截器逻辑--------
		
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
	 * 组装占位符参数 -> Map
	 * 
	 * @param executeSql
	 * @return
	 * @throws OgnlException
	 */
	private Map<String, Object> installPlaceholderSqlParam(String executeSql, Map sqlParamsMap) throws OgnlException {
		Map<String, Object> map = new HashMap<String, Object>();
		//update-begin---author:scott----date:20160906------for:参数不支持下划线解决--------
		String regEx = ":[ tnx0Bfr]*[0-9a-z.A-Z_]+"; // 表示以：开头，[0-9或者.或者A-Z大小都写]的任意字符，超过一个
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
	 */
	private String parseSqlTemplate(Method method, String templateSql, Map<String, Object> sqlParamsMap) {
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

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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

}
