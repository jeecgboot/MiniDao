package org.jeecgframework.minidao.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.def.MiniDaoConstants;
import org.jeecgframework.minidao.hibernate.dao.IGenericBaseCommonDao;
import org.jeecgframework.minidao.util.FreemarkerParseUtil;
import org.jeecgframework.minidao.util.JdkLocalUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;



/**
 * 
 * @Title:MiniDaoHandler
 * @description:MiniDAO 拦截器
 * @author 张代浩
 * @date 20130817
 * @version V1.0
 */
public class MiniDaoHandler implements MethodInterceptor {

	private JdbcTemplate jdbcTemplate;
	private IGenericBaseCommonDao genericBaseCommonDao;

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Method method = methodInvocation.getMethod();
		Object[] args = methodInvocation.getArguments();
		//返回结果
		Object returnObj = null;
		//SQL模板
		String templateSql = null;
		//SQL模板参数
		Map<String, Object> sqlParamsMap = new HashMap<String, Object>();
		
		
		//check.1:判断是否是抽象方法，如果是非抽象方法，则不执行MiniDao拦截器
		if (!JdkLocalUtil.isAbstract(method)) {
	            return methodInvocation.proceed();
	    }
		//Step.0 判断是否是Hiber实体维护方法，如果是执行Hibernate方式实体维护
		Map rs = new HashMap();
		if(miniDaoHiber(rs, method, args)){
			return rs.get("returnObj");
		}
		//Step.1装载SQL模板，所需参数
		templateSql = installDaoMetaData(method, sqlParamsMap, args);
	
		//Step.3解析SQL模板，返回可执行SQL
		String executeSql = parseSqlTemplate(method, templateSql, sqlParamsMap);

		//Step.3 获取SQL执行返回值
		returnObj = getReturnMinidaoResult(jdbcTemplate, method, executeSql);
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
			 genericBaseCommonDao.save(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_GET_BY_ID_HIBER.equals(method.getName())){
			 //获取Dao方法与实体配置
			 Class<?> clz = (Class<?>) args[0];
			 rs.put("returnObj", genericBaseCommonDao.get(clz, args[1].toString()));
			 return true;
		}
		if(MiniDaoConstants.METHOD_GET_BY_ENTITY_HIBER.equals(method.getName())){
			 //获取主键名
			 rs.put("returnObj", genericBaseCommonDao.get(args[0]));
			 return true;
		}
		if(MiniDaoConstants.METHOD_UPDATE_BY_HIBER.equals(method.getName())){
			 genericBaseCommonDao.saveOrUpdate(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_DELETE_BY_HIBER.equals(method.getName())){
			 genericBaseCommonDao.delete(args[0]);
			 return true;
		}
		if(MiniDaoConstants.METHOD_DELETE_BY_ID_HIBER.equals(method.getName())){
			 Class<?> clz = (Class<?>) args[0];
			 genericBaseCommonDao.deleteEntityById(clz, args[1].toString());
			 return true;
		}
		if(MiniDaoConstants.METHOD_LIST_BY_HIBER.equals(method.getName())){
			 rs.put("returnObj", genericBaseCommonDao.loadAll(args[0]));
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
	private String parseSqlTemplate(Method method,String templateSql,Map sqlParamsMap){
		// step.1.根据命名规范[接口名_方法名.sql]，获取SQL模板文件的路径
		String executeSql = null;
		//String sqlTempletPath = "/examples/sql/EmployeeDao_getCount.sql";
		String sqlTempletPath = "/"+method.getDeclaringClass().getName().replace(".", "/").replace("/dao/", "/sql/")+"_"+method.getName()+".sql";
		
		//System.out.println("*********MiniDao-SQL-Templet-Path***************************************"+sqlTempletPath);
		// step.2.将args转换成键值对，封装成Map对象
		
		// step.3.通过模板引擎给SQL模板装载参数,解析生成可执行SQL
		if(StringUtils.isNotEmpty(templateSql)){
			executeSql = new FreemarkerParseUtil().parseTemplateContent(templateSql, sqlParamsMap);
		}else{
			executeSql = new FreemarkerParseUtil().parseTemplate(sqlTempletPath, sqlParamsMap);
		}
		System.out.println("*********MiniDao--Execute--SQL**********************************************************************");
		System.out.println(executeSql);
		System.out.println("*********MiniDao--Execute--SQL**********************************************************************");
		return executeSql;
	}
	
	
	/**
	 *  获取MiniDao处理结果集
	 * @param jdbcTemplate
	 * @param method
	 * @param executeSql
	 * @return  结果集
	 */
	private Object getReturnMinidaoResult(JdbcTemplate jdbcTemplate,Method method,String executeSql){
		// step.4.调用SpringJdbc引擎，执行SQL返回值
		// 5.1获取返回值类型[Map/Object/List<Object>/List<Map>/基本类型]
		String methodName = method.getName();
		// 判斷是否非查詢方法
		if (checkActiveKey(methodName)) {
			return jdbcTemplate.update(executeSql);
		} else if (checkBatchKey(methodName)) {
			return jdbcTemplate.batchUpdate(executeSql.split(";"));
		} else {
			// 如果是查詢操作
			Class<?> returnType = method.getReturnType();
//			System.out.println(returnType.getName());

			if (returnType.isPrimitive()) {
//				System.out.println("------------isPrimitive------------- ");
				// 判断是否为基本类型
				if ("int".equals(returnType)) {
					return jdbcTemplate.queryForInt(executeSql);
				}
				if ("long".equals(returnType)) {
					return jdbcTemplate.queryForLong(executeSql);
				}
			} else if (returnType.isAssignableFrom(List.class)) {
				// 判断是否是List类型
				// 判断泛型
				return jdbcTemplate.queryForList(executeSql);
			} else if (returnType.isAssignableFrom(Map.class)) {
				//Map类型
				return jdbcTemplate.queryForMap(executeSql);
			}else if(JdkLocalUtil.isWrapClass(returnType)){
				//基本类型的包装类
				return jdbcTemplate.queryForObject(executeSql, returnType);
			} else {
				// 对象类型
				RowMapper<?> rm = ParameterizedBeanPropertyRowMapper.newInstance(returnType);
				return jdbcTemplate.queryForObject(executeSql, rm);
			}
		}
		return null;
	}

	
	/**
	 * 装载SQL模板参数
	 * 
	 * @param method
	 * @param sqlParamsMap 返回(装载模板参数)
	 * @param args
	 * @return   templateSql(@SQL标签的SQL)
	 * @throws Exception
	 */
	private String installDaoMetaData(Method method,Map<String, Object> sqlParamsMap,Object[] args) throws Exception{
		String templateSql = null;
		//如果方法参数大于1个的话，方法必须使用注释标签Arguments
		boolean arguments_flag = method.isAnnotationPresent(Arguments.class);
		if(arguments_flag){
			//[1].获取方法的参数标签
			Arguments arguments = method.getAnnotation(Arguments.class);  
            System.out.println("@Arguments------------------------------------------"+Arrays.toString(arguments.value()));
            if(arguments.value().length > args.length){
            	//校验机制-如果注释标签参数数目大于方法的参数，则抛出异常
            	throw new Exception("[注释标签]参数数目，不能大于[方法参数]参数数目");
            }
            // step.2.将args转换成键值对，封装成Map对象
            int args_num = 0;
            for(String v:arguments.value()){
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
            System.out.println("@Sql------------------------------------------"+sql.value());  
		}
		return templateSql;
	}
	

	/**
	 * 判斷是否是執行的方法（非查詢）
	 * 
	 * @param methodName
	 * @return
	 */
	public static boolean checkActiveKey(String methodName) {
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
	public static boolean checkBatchKey(String methodName) {
		String keys[] = MiniDaoConstants.INF_METHOD_BATCH.split(",");
		for (String s : keys) {
			if (methodName.startsWith(s))
				return true;
		}
		return false;
	}
	

	public IGenericBaseCommonDao getGenericBaseCommonDao() {
		return genericBaseCommonDao;
	}


	public void setGenericBaseCommonDao(IGenericBaseCommonDao genericBaseCommonDao) {
		this.genericBaseCommonDao = genericBaseCommonDao;
	}


	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
