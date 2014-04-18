package org.jeecgframework.minidao.aop;

import java.io.File;
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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.def.MiniDaoConstants;
import org.jeecgframework.minidao.hibernate.dao.IGenericBaseCommonDao;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.spring.rowMapper.GenericRowMapper;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnMapRowMapper;
import org.jeecgframework.minidao.spring.rowMapper.MiniColumnOriginalMapRowMapper;
import org.jeecgframework.minidao.util.FreemarkerParseFactory;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;



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
public class MiniDaoHandler implements MethodInterceptor {
	private static final Logger logger = Logger.getLogger(MiniDaoHandler.class);
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private IGenericBaseCommonDao miniDaoHiberCommonDao;
	
	private BasicFormatterImpl formatter = new BasicFormatterImpl();
	
	private String UPPER_KEY = "upper";
	private String LOWER_KEY = "lower";
	
	/**
	 * map的关键字类型  三个值
	 */
	private String keyType = "origin";
	private boolean formatSql = false;
	private boolean showSql = false;
	private String dbType;

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Method method = methodInvocation.getMethod();
		Object[] args = methodInvocation.getArguments();
		//返回结果
		Object returnObj = null;
		//SQL模板
		String templateSql = null;
		//SQL模板参数
		Map<String, Object> sqlParamsMap = new HashMap<String, Object>();
		//分页参数
		MiniDaoPage pageSetting = new MiniDaoPage();
		
		//check.1:判断是否是抽象方法，如果是非抽象方法，则不执行MiniDao拦截器
		if (!MiniDaoUtil.isAbstract(method)) {
	            return methodInvocation.proceed();
	    }
		//Step.0 判断是否是Hiber实体维护方法，如果是执行Hibernate方式实体维护
		Map<String,Object> rs = new HashMap<String,Object>();
		if(miniDaoHiber(rs, method, args)){
			return rs.get("returnObj");
		}
		//Step.1装载SQL模板，所需参数
		templateSql = installDaoMetaData(pageSetting,method, sqlParamsMap, args);
	
		//Step.3解析SQL模板，返回可执行SQL
		String executeSql = parseSqlTemplate(method, templateSql, sqlParamsMap);

		//Step.4 组装SQL占位符参数
		Map<String,Object> sqlMap = installPlaceholderSqlParam(executeSql,sqlParamsMap);
		
		//Step.5 获取SQL执行返回值
		returnObj = getReturnMinidaoResult(dbType,pageSetting,jdbcTemplate, method, executeSql,sqlMap);
		//TODO SQL缓存机制
		if(showSql){
			logger.info("MiniDao-SQL:\n\n"+(formatSql == true?formatter.format(executeSql):executeSql)+"\n");
		}
		return returnObj;
	}
	
	
	
	/**
	 * MiniDao支持实体维护
	 * 说明：向下兼容Hibernate实体维护方式,实体的增删改查SQL自动生成,不需要写SQL
	 * @param returnObj
	 * @param method
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean miniDaoHiber(Map rs,Method method,Object[] args){
		//是否采用Hibernate方式，进行实体维护，不需要生成SQL
		//boolean arguments_flag = entity.isAnnotationPresent(Entity.class);
		//判断如果是持久化对象，则调用Hibernate进行持久化维护
		if(MiniDaoConstants.METHOD_SAVE_BY_HIBER.equals(method.getName())){
			 miniDaoHiberCommonDao.save(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_GET_BY_ID_HIBER.equals(method.getName())){
			 //获取Dao方法与实体配置
			 Class<?> clz = (Class<?>) args[0];
			 rs.put("returnObj", miniDaoHiberCommonDao.get(clz, args[1].toString()));
			 return true;
		}
		if(MiniDaoConstants.METHOD_GET_BY_ENTITY_HIBER.equals(method.getName())){
			 //获取主键名
			 rs.put("returnObj", miniDaoHiberCommonDao.get(args[0]));
			 return true;
		}
		if(MiniDaoConstants.METHOD_UPDATE_BY_HIBER.equals(method.getName())){
			 miniDaoHiberCommonDao.saveOrUpdate(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_DELETE_BY_HIBER.equals(method.getName())){
			 miniDaoHiberCommonDao.delete(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_DELETE_BY_ID_HIBER.equals(method.getName())){
			 Class<?> clz = (Class<?>) args[0];
			 miniDaoHiberCommonDao.deleteEntityById(clz, args[1].toString());
			 return true;
		}
		if(MiniDaoConstants.METHOD_LIST_BY_HIBER.equals(method.getName())){
			 rs.put("returnObj", miniDaoHiberCommonDao.loadAll(args[0]));
			 return true;
		}
		return false;
	}
	
	/**
	 * 解析SQL模板
	 * 
	 * @param method
	 * @param templateSql
	 * @param sqlParamsMap
	 * @return  可执行SQL
	 */
	private String parseSqlTemplate(Method method,String templateSql,Map<String,Object> sqlParamsMap){
		// step.1.根据命名规范[接口名_方法名.sql]，获取SQL模板文件的路径
		String executeSql = null;
		
		// step.2.获取SQL模板内容
		// step.3.通过模板引擎给SQL模板装载参数,解析生成可执行SQL
		if(StringUtils.isNotEmpty(templateSql)){
			executeSql = new FreemarkerParseFactory().parseTemplateContent(templateSql, sqlParamsMap);
		}else{
			// update-begin--Author:fancq  Date:20131225 for：sql放到dao层同样目录
			// update-begin--Author:zhaojunfu  Date:20140418 for：扫描规则-首先扫描同位置sql目录,如果没找到文件再搜索dao目录
			String sqlTempletPath = "/"+method.getDeclaringClass().getName().replace(".", "/").replace("/dao/", "/sql/")+"_"+method.getName()+".sql";
			File sqlDirFile = new File(sqlTempletPath);
			if(!sqlDirFile.exists()){
				sqlTempletPath = "/"+method.getDeclaringClass().getName().replace(".", "/")+"_"+method.getName()+".sql";
			}
			// update-end--Author:fancq  Date:20131225 for：sql放到dao层同样目录
			// update-end--Author:zhaojunfu  Date:20140418 for：扫描规则：首先扫描同位置sql目录,如果没找到文件再搜索dao目录
			logger.debug("MiniDao-SQL-Path:"+sqlTempletPath);
			executeSql = new FreemarkerParseFactory().parseTemplate(sqlTempletPath, sqlParamsMap);
		}
		return getSqlText(executeSql);
	}
	
	
	/**
	 * 除去无效字段，不然批量处理可能报错
	 */
	private String getSqlText(String sql) {
		return sql.replaceAll("\\n", " ").replaceAll("\\t", " ")
				.replaceAll("\\s{1,}", " ").trim();
	}
	
	
	/**
	 * 组装占位符参数 -> Map
	 * @param executeSql
	 * @return
	 * @throws OgnlException 
	 */
	private Map<String,Object> installPlaceholderSqlParam(String executeSql,Map sqlParamsMap) throws OgnlException{
		Map<String,Object> map = new HashMap<String,Object>();
		String regEx = ":[ tnx0Bfr]*[0-9a-z.A-Z]+"; // 表示以：开头，[0-9或者.或者A-Z大小都写]的任意字符，超过一个
		Pattern pat = Pattern.compile(regEx);
		Matcher m = pat.matcher(executeSql);
		while (m.find()) {
			logger.debug(" Match [" + m.group() +"] at positions " + m.start() + "-" + (m.end() - 1));
			String ognl_key = m.group().replace(":","").trim();
			map.put(ognl_key, Ognl.getValue(ognl_key, sqlParamsMap));
		}
		return map;
	}
	
	/**
	 *  获取MiniDao处理结果集
	 * @param dbType 
	 * @param pageSetting 
	 * @param jdbcTemplate
	 * @param method
	 * @param executeSql
	 * @return  结果集
	 */
	private Object getReturnMinidaoResult(String dbType, MiniDaoPage pageSetting, JdbcTemplate jdbcTemplate,Method method,String executeSql,Map<String,Object> paramMap){
		// step.4.调用SpringJdbc引擎，执行SQL返回值
		// 5.1获取返回值类型[Map/Object/List<Object>/List<Map>/基本类型]
		String methodName = method.getName();
		// 判斷是否非查詢方法
		if (checkActiveKey(methodName)) {
			if(paramMap!=null){
				return namedParameterJdbcTemplate.update(executeSql, paramMap);
			}else{
				return jdbcTemplate.update(executeSql);
			}
		} else if (checkBatchKey(methodName)) {
			return batchUpdate(jdbcTemplate,executeSql);
		} else {
			// 如果是查詢操作
			Class<?> returnType = method.getReturnType();
			if (returnType.isPrimitive()) {
				Number number = jdbcTemplate.queryForObject(executeSql,BigDecimal.class);
				if ("int".equals(returnType)) {
					return number.intValue();
				} else if ("long".equals(returnType)) {
					return number.longValue();
				} else if ("double".equals(returnType)) {
					return number.doubleValue();
				}
			} else if (returnType.isAssignableFrom(List.class)) {
				// update-begin--Author:fancq  Date:20140102 for：支持多数据分页
				int page = pageSetting.getPage();
				int rows = pageSetting.getRows();
				if(page!=0 && rows!=0){
					executeSql = MiniDaoUtil.createPageSql(dbType,executeSql, page, rows);
				}
				// update-begin--Author:fancq  Date:20140102 for：支持多数据分页
				// update-begin--Author:fancq  Date:20131219 for：支持返回Map和实体 list
				ResultType resultType = method.getAnnotation(ResultType.class);
				String[] values = null;
				if (resultType != null) {
					values = resultType.value();
				}
				if (values == null || values.length == 0 || "java.util.Map".equals(values[0])) {
					if(paramMap!=null){
						return namedParameterJdbcTemplate.query(executeSql, paramMap,getColumnMapRowMapper());
					}else{
						return jdbcTemplate.query(executeSql,getColumnMapRowMapper());
					}
				} else {
					Class clazz = null;
					try {
						clazz = Class.forName(values[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(paramMap!=null){
						return namedParameterJdbcTemplate.query(executeSql, paramMap, new GenericRowMapper(clazz));
					}else{
						return jdbcTemplate.query(executeSql, new GenericRowMapper(clazz));
					}
				}
				// update-end--Author:fancq  Date:20131219 for：支持返回Map和实体 list
			} else if (returnType.isAssignableFrom(Map.class)) {
				//Map类型
				if(paramMap!=null){
					return (Map)namedParameterJdbcTemplate.queryForObject(executeSql, paramMap,getColumnMapRowMapper());
				}else{
					return (Map)jdbcTemplate.queryForObject(executeSql,getColumnMapRowMapper());
				}
			} else if (returnType.isAssignableFrom(String.class)) {
				//String类型
				try{  
					if(paramMap!=null){
						return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, String.class);
					}else{
						return jdbcTemplate.queryForObject(executeSql,String.class);
					}
		        }catch (EmptyResultDataAccessException e) {  
		            return null;  
		        }
			}else if(MiniDaoUtil.isWrapClass(returnType)){
				//基本类型的包装类
				try{  
					if(paramMap!=null){
						return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, returnType);
					}else{
						return jdbcTemplate.queryForObject(executeSql, returnType);
					}
		        }catch (EmptyResultDataAccessException e) {  
		            return null;  
		        }
			} else {
				// 对象类型
				RowMapper<?> rm = ParameterizedBeanPropertyRowMapper.newInstance(returnType);
				try{  
					if(paramMap!=null){
						return namedParameterJdbcTemplate.queryForObject(executeSql, paramMap, rm);
					}else{
						return jdbcTemplate.queryForObject(executeSql, rm);
					}
		        }catch (EmptyResultDataAccessException e) {  
		            return null;  
		        }
			}
		}
		return null;
	}

	/**
	 * 批处理
	 *@Author JueYue
	 *@date   2013-11-17
	 *@return
	 */
	private int[] batchUpdate(JdbcTemplate jdbcTemplate, String executeSql) {
		String[] sqls = executeSql.split(";");
		if(sqls.length<100){
			return jdbcTemplate.batchUpdate(sqls);
		}
		int[] result = new int[sqls.length];
		List<String> sqlList = new ArrayList<String>();
		for(int i = 0;i<sqls.length;i++){
			sqlList.add(sqls[i]);
			if(i%100 == 0){
				addResulArray(result,i+1,jdbcTemplate.batchUpdate(sqlList.toArray(new String[0])));
				sqlList.clear();
			}
		}
		addResulArray(result,sqls.length,jdbcTemplate.batchUpdate(sqlList.toArray(new String[0])));
		return result;
	}


	/**
	 * 把批量处理的结果拼接起来
	 *@Author JueYue
	 *@date   2013-11-17
	 */
	private void addResulArray(int[] result,int index, int[] arr) {
		int length = arr.length;
		for(int i = 0;i<length;i++){
			result[index-length + i] = arr[i];
		}
	}



	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}



	public void setNamedParameterJdbcTemplate(
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}



	/**
	 * 装载SQL模板参数
	 * @param pageSetting 
	 * 
	 * @param method
	 * @param sqlParamsMap 返回(装载模板参数)
	 * @param args
	 * @return   templateSql(@SQL标签的SQL)
	 * @throws Exception
	 */
	private String installDaoMetaData(MiniDaoPage pageSetting, Method method,Map<String, Object> sqlParamsMap,Object[] args) throws Exception{
		String templateSql = null;
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
            int args_num = 0;
            for(String v:arguments.value()){
            	// update-begin--Author:fancq  Date:20140102 for：支持多数据分页
            	if (v.equalsIgnoreCase("page")) {
            		pageSetting.setPage(Integer.parseInt(args[args_num].toString()));
            	}
            	if (v.equalsIgnoreCase("rows")) {
            		pageSetting.setRows(Integer.parseInt(args[args_num].toString()));
            	}
            	// update-end--Author:fancq  Date:20140102 for：支持多数据分页
            	sqlParamsMap.put(v, args[args_num]);
            	args_num++;
            }
		}else{
			//如果未使用[参数标签]
			if(args.length>1){
				throw new Exception("方法参数数目>=2，方法必须使用注释标签@Arguments");	
			}else if(args.length==1){
				// step.2.将args转换成键值对，封装成Map对象
				sqlParamsMap.put(MiniDaoConstants.SQL_FTL_DTO, args[0]);
			}
			
		}
		
		//[2].获取方法的SQL标签
		if(method.isAnnotationPresent(Sql.class)){
			Sql sql = method.getAnnotation(Sql.class);
			//如果用户采用自定义标签SQL，则SQL文件无效
			if(StringUtils.isNotEmpty(sql.value())){
				templateSql = sql.value();
			}
            logger.debug("@Sql------------------------------------------"+sql.value());  
		}
		return templateSql;
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
	 *根据参数设置map的key大小写
	 **/
	private RowMapper<Map<String,Object>> getColumnMapRowMapper() {
		if(getKeyType().equalsIgnoreCase(LOWER_KEY)){
			return new MiniColumnMapRowMapper();
		}else if(getKeyType().equalsIgnoreCase(UPPER_KEY)){
			return new ColumnMapRowMapper();
		}else{
			return new MiniColumnOriginalMapRowMapper();
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public IGenericBaseCommonDao getMiniDaoHiberCommonDao() {
		return miniDaoHiberCommonDao;
	}

	public void setMiniDaoHiberCommonDao(IGenericBaseCommonDao miniDaoHiberCommonDao) {
		this.miniDaoHiberCommonDao = miniDaoHiberCommonDao;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isFormatSql() {
		return formatSql;
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}



	public String getKeyType() {
		return keyType;
	}



	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}



	public String getDbType() {
		return dbType;
	}



	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	
	
}
