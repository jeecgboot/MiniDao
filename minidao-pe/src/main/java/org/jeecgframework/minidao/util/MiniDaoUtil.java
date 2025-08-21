package org.jeecgframework.minidao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.pagehelper.PageException;
import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pagehelper.dialect.PageAutoDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor49;
import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
import org.jeecgframework.minidao.sqlparser.impl.util.SqlParserUtils;
import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
import org.jeecgframework.minidao.sqlparser.impl.vo.SelectSqlInfo;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Title:JdkLocalUtil
 * @description:JdkLocalUtil
 * @author 张代浩
 * @date Jul 5, 2013 2:58:29 PM
 * @version V1.0
 */
public class MiniDaoUtil {
	private static final Log logger = LogFactory.getLog(MiniDaoUtil.class);
	/**
	 * 缓存有没有问题
	 */
	static PageAutoDialect pageAutoDialect = new PageAutoDialect();

	/**
	 * 数据库类型别名
	 */
	public static final String DATABSE_TYPE_HSQLDB = "hsqldb";
	public static final String DATABSE_TYPE_H2 = "h2";
	public static final String DATABSE_TYPE_PHOENIX = "phoenix";
	public static final String DATABSE_TYPE_POSTGRE = "postgresql";

	public static final String DATABSE_TYPE_MYSQL = "mysql";
	public static final String DATABSE_TYPE_MARIADB = "mariadb";
	public static final String DATABSE_TYPE_SQLITE = "sqlite";
	public static final String DATABSE_TYPE_HERDDB = "herddb";

	public static final String DATABSE_TYPE_ORACLE = "oracle";
	public static final String DATABSE_TYPE_ORACLE9I= "oracle9i";
	public static final String DATABSE_TYPE_DM = "dm"; //达梦数据库
	public static final String DATABSE_TYPE_DB2 = "db2";
	public static final String DATABSE_TYPE_INFORMIX = "informix";
	public static final String DATABSE_TYPE_INFORMIX_SQLI = "informix-sqli";

	public static final String DATABSE_TYPE_SQLSERVER = "sqlserver";
	public static final String DATABSE_TYPE_SQLSERVER2012 = "sqlserver2012";

	public static final String DATABSE_TYPE_DERBY= "derby"; //Derby
	public static final String DATABSE_TYPE_EDB= "edb";
	public static final String DATABSE_TYPE_OSCAR = "oscar"; //神通
	public static final String DATABSE_TYPE_KINGBASE = "kingbase";//人大金仓
	public static final String DATABSE_TYPE_CLICKHOUSE = "clickhouse";
	public static final String DATABSE_TYPE_HIGHGO = "highgo";//瀚高数据库
	public static final String DATABSE_TYPE_XUGU = "xugu";//瀚高数据库
	public static final String DATABSE_TYPE_ZENITH = "zenith"; //华为高斯 GaussDB
	public static final String DATABSE_TYPE_POLARDB = "polardb"; //PolarDB
	//涛思数据库TDengine
	public static final String DATABSE_TYPE_TDENGINE= "taos";


	//update-begin---author:scott ---date:2024-07-04  for：SQL解析引擎改造支持普通和jsqlparser切换----
//	private static final boolean JSQLPARSER_AVAILABLE = checkJSqlParserAvailability();
	private static final boolean JSQLPARSER49_AVAILABLE = checkJSqlParser49Availability();
	protected static AbstractSqlProcessor abstractSqlProcessor;
	static {
		if (MiniDaoUtil.isJSqlParser49Available()) {
			abstractSqlProcessor = new JsqlparserSqlProcessor49();
		} 
//		else if (MiniDaoUtil.isJSqlParserAvailable()) {
//			abstractSqlProcessor = new JsqlparserSqlProcessor();
//		} 
		else {
			abstractSqlProcessor = new SimpleSqlProcessor();
		}
	}
	//update-end---author:scott ---date::2024-07-04  for：SQL解析引擎改造支持普通和jsqlparser切换----
	
//	/**
//	 * 分页SQL
//	 */
//	public static final String MYSQL_SQL = "select * from ( {0}) sel_tab00 limit {1},{2}"; // mysql
//	public static final String POSTGRE_SQL = "select * from ( {0}) sel_tab00 limit {2} offset {1}";// postgresql
//	public static final String ORACLE_SQL = "select * from (select row_.*,rownum rownum_ from ({0}) row_ where rownum <= {1}) where rownum_>{2}"; // oracle
//	public static final String SQLSERVER_SQL = "select * from ( select row_number() over(order by tempColumn) tempRowNumber, * from (select top {1} tempColumn = 0, {0}) t ) tt where tempRowNumber > {2}"; // sqlserver


	/**
	 * 获取url
	 *
	 * @param dataSource
	 * @return
	 */
	private static String getUrl(DataSource dataSource) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return conn.getMetaData().getURL();
		} catch (SQLException e) {
			throw new PageException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					//ignore
				}
			}
		}
	}


	/**
	 * 自动获取DB类型
	 * @param dbUrl 数据库连接
	 * @return
	 */
	public static String getDbType(String dbUrl){
		String dbType = pageAutoDialect.getDialectKeyByJdbcUrl(dbUrl);
		if(dbType!=null){
			dbType = dbType.toLowerCase();
		}
		return dbType;
	}

//    /**
//     * 自动获取DB类型
//     * @param dbUrl 数据库连接
//     * @return
//     */
//    public static String getDbType(String dbUrl){
//        String dbType = "";
//		dbUrl = dbUrl.toLowerCase();
//        if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_MYSQL)) {
//            dbType = MiniDaoUtil.DATABSE_TYPE_MYSQL;
//        }else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_ORACLE)) {
//            dbType = MiniDaoUtil.DATABSE_TYPE_ORACLE;
//        }else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_SQLSERVER)) {
//            dbType = MiniDaoUtil.DATABSE_TYPE_SQLSERVER;
//        }else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_POSTGRE)) {
//            dbType = MiniDaoUtil.DATABSE_TYPE_POSTGRE;
//        }else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_DM)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_DM;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_MARIADB)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_MARIADB;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_DB2)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_DB2;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_SQLITE)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_SQLITE;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_KINGBASE8)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_KINGBASE8;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_POLARDB)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_POLARDB;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_OSCAR)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_OSCAR;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_ZENITH)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_ZENITH;
//		}else if(dbUrl.contains(MiniDaoUtil.DATABSE_TYPE_DERBY)) {
//			dbType = MiniDaoUtil.DATABSE_TYPE_DERBY;
//		}else {
//            dbType = MiniDaoUtil.DATABSE_TYPE_OTHER;
//        }
//        return dbType;
//    }


	/**
	 * 自动获取DB类型
	 * @param dataSource
	 * @return
	 */
	public static String getDbType(DataSource dataSource){
        long startTime=System.currentTimeMillis();
		//获取数据库连接url
		String dbUrl = getUrl(dataSource);
		String dbType = getDbType(dbUrl);
        long endTime=System.currentTimeMillis();
        logger.debug("获取DB类型："+ dbType+ "，耗时："+ (endTime-startTime) +"ms");
		return dbType;
	}

		/**
	 * 按照数据库类型，封装SQL
	 *
	 * @param dbUrl
	 *            数据库类型
	 * @param sql
	 * @param page
	 * @param rows
	 * @return
	 */
	public static String createPageSql(String dbUrl, String sql, int page,int rows) {
		AbstractHelperDialect dialect = pageAutoDialect.getDialect(dbUrl);
		MiniDaoPage pageSetting = new MiniDaoPage();
		pageSetting.setPage(page);
		pageSetting.setRows(rows);
		//---------------------------------------------------------------------------------------------
		// 如果包含mybatis变量，先将其替换为占位符，避免解析时出错
		Map<String,String> tokenToRaw = new LinkedHashMap<>();
		sql = SqlParserUtils.maskMyBatisPlaceholders(sql, tokenToRaw);
		//---------------------------------------------------------------------------------------------
		String executePageSql = dialect.getPageSql(sql,pageSetting);
		//---------------------------------------------------------------------------------------------
		// 如果包含mybatis变量，恢复占位符
		executePageSql = SqlParserUtils.restoreMyBatisPlaceholders(executePageSql, tokenToRaw);
		//---------------------------------------------------------------------------------------------
		return executePageSql;
	}

	/**
	 * 获取SQL查询记录数SQL
	 *
	 * @param sql
	 * @return
	 */
	public static String getCountSql(String sql) {
		try {
			sql = abstractSqlProcessor.getCountSql(sql);
		} catch (Exception e) {
			logger.warn("getCountSql error:" + e.getMessage());
		}
		return sql;
	}

	/**
	 * 去除SQL中的order by (为了兼容SQLServer)
	 *
	 * @param sql
	 * @return
	 */
	public static String removeOrderBy(String sql) {
		try {
			sql = abstractSqlProcessor.removeOrderBy(sql);
		} catch (Exception e) {
			logger.warn("removeOrderBy error:" + e.getMessage());
		}
		return sql;
	}

	/**
	 * 在SQL的最外层增加或修改ORDER BY子句
	 * @for TV360X-2551
	 * @param sql 原始SQL
	 * @param field 新的ORDER BY字段
	 * @param isAsc 是否正序
	 * @return
	 * @author chenrui
	 * @date 2024/9/27 17:25
	 */
	public static String addOrderBy(String sql, String field, boolean isAsc){
		try {
			sql = abstractSqlProcessor.addOrderBy(sql,field,isAsc);
		} catch (Exception e) {
			logger.warn("addOrderBy error:" + e.getMessage());
		}
		return sql;
	}

	/**
	 * 为SQL语句增加查询条件（直接使用条件语句）
	 * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
	 * @param sql 原始SQL
	 * @param condition 查询条件（不含where关键字）
	 * @return 添加查询条件后的SQL
	 */
	public static String addWhereCondition(String sql, String condition) {
		try {
			sql = abstractSqlProcessor.addWhereCondition(sql, condition);
		} catch (Exception e) {
			logger.warn("addWhereCondition error:" + e.getMessage());
		}
		return sql;
	}

	/**
	 * 为SQL语句增加查询条件（使用字段、值和操作符）
	 * for [issues/8336]支持SqlServer数据使用sql排序，新方案。
	 * @param sql 原始SQL
	 * @param field 字段名
	 * @param value 字段值
	 * @param operator 比较操作符（如：=, >, <, !=, like等）
	 * @return 添加查询条件后的SQL
	 */
	public static String addWhereCondition(String sql, String field, Object value, String operator) {
		try {
			sql = abstractSqlProcessor.addWhereCondition(sql, field, value, operator);
		} catch (Exception e) {
			logger.warn("addWhereCondition error:" + e.getMessage());
		}
		return sql;
	}

	/**
	 * 解析SQL查询字段
	 *
	 * @param parsedSql
	 * @return
	 */
	public static List<Map<String, Object>> parseSqlFields(String parsedSql) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			list = abstractSqlProcessor.parseSqlFields(parsedSql);
		} catch (Exception e) {
			logger.warn("parseSqlFields error:" + e.getMessage());
		}
		return list;
	}
	
	/**
	 * 解析SQL查询字段
	 *
	 * @param parsedSql
	 * @return
	 */
	public static Map<String, Object> parsSqlField(String parsedSql) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			list = abstractSqlProcessor.parseSqlFields(parsedSql);
		} catch (Exception e) {
			logger.warn("parseSqlFields error:" + e.getMessage());
		}
		if(!CollectionUtils.isEmpty(list) && list.size() > 0){
			return list.get(0);
		}

		return null;
	}

	/**
	 * 解析 SQL 查询，将 SELECT 字段映射到 (alias或列名) -> (完整字段表达式) 的 Map 中。
	 * for [QQYUN-13476]online 报表SqlServer兼容改造完
	 *
	 * @param parsedSql 要解析的 SQL 查询语句
	 * @return 字段映射：key 为别名（若有）或列名（无表名前缀），value 为对应的完整表达式字符串
	 * @author chenrui
	 * @date 2025/8/20 16:41
	 */
	public static Map<String, String> parseSelectAliasMap(String parsedSql) {
		Map<String, String> resp = new LinkedHashMap<>();
		try {
			resp = abstractSqlProcessor.parseSelectAliasMap(parsedSql);
		} catch (Exception e) {
			logger.warn("parseSelectAliasMap error:" + e.getMessage());
		}
		return resp;
	}
	
	/**
	 * 解析SQL查询字段
	 *
	 * @param parsedSql
	 * @return Map<String, SelectSqlInfo>
	 */
	public static Map<String, SelectSqlInfo> parseAllSelectTable(String parsedSql) {
        try {
            return abstractSqlProcessor.parseAllSelectTable(parsedSql);
        } catch (Exception e) {
			logger.warn("parseTable error:" + e.getMessage());
        }
		return null;
	}
	
	/**
	 * 解析SQL查询字段
	 *
	 * @param parsedSql
	 * @return SelectSqlInfo
	 */
	public static SelectSqlInfo parseSelectSqlInfo(String parsedSql) {
        try {
            return abstractSqlProcessor.parseSelectSqlInfo(parsedSql);
        } catch (Exception e) {
			logger.warn("parseTable error:" + e.getMessage());
        }
		return null;
	}

	/**
	 * 根据 sql语句 获取表和字段信息
	 *
	 * @param sql sql语句
	 */
	public static List<QueryTable> getQueryTableInfo(String sql) {
		try {
			return abstractSqlProcessor.getQueryTableInfo(sql);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("getQueryTableInfo error:" + e.getMessage());
		}
		return null;
	}

//	/**
//	 * 判断当前环境是否支持jsqlparser
//	 *
//	 * @return
//	 */
//	public static boolean isJSqlParserAvailable() {
//		return JSQLPARSER_AVAILABLE;
//	}

	/**
	 * 判断当前环境是否支持jsqlparser4.9
	 *
	 * @return
	 */
	public static boolean isJSqlParser49Available() {
		return JSQLPARSER49_AVAILABLE;
	}

//	/**
//	 * 判断当前环境是否存在jsqlparser，返回true或false
//	 *
//	 * @return
//	 */
//	private static boolean checkJSqlParserAvailability() {
//		try {
//			Class.forName("net.sf.jsqlparser.statement.select.SelectBody");
//			logger.debug("【Sql Parser】 The environment supports jsqlparser engine");
//			return true;
//		} catch (ClassNotFoundException e) {
//			logger.warn("【Sql Parser】 The environment does not support jsqlparser engine");
//			return false;
//		}
//	}

	/**
	 * 判断当前环境是否存在jsqlparser 4.9，返回true或false
	 *
	 * @return boolean
	 */
	private static boolean checkJSqlParser49Availability() {
		try {
			// 此为 4.7 新增的类，4.9中也有，但 4.6 中没有
			Class.forName("net.sf.jsqlparser.expression.RangeExpression");
			logger.debug("【Sql Parser】 The environment supports jsqlparser 4.9 engine");
			return true;
		} catch (ClassNotFoundException e) {
			logger.warn("【Sql Parser】 The environment does not support jsqlparser 4.9 engine");
			return false;
		}
	}

//	/**
//	 * 按照数据库类型，封装SQL
//	 *
//	 * @param dbType
//	 *            数据库类型
//	 * @param sql
//	 * @param page
//	 * @param rows
//	 * @return
//	 */
//	public static String createPageSql(String dbType, String sql, int page,int rows) {
//		int beginNum = (page - 1) * rows;
//		String[] sqlParam = new String[3];
//		sqlParam[0] = sql;
//		sqlParam[1] = beginNum + "";
//		sqlParam[2] = rows + "";
//		if (dbType == null || "".equals(dbType)) {
//			throw new RuntimeException("org.jeecgframework.minidao.aop.MiniDaoHandler:(数据库类型:dbType)没有设置,请检查配置文件");
//		}
//		if (DATABSE_TYPE_OTHER.equals(dbType)) {
//			return sql;
//		}else if (DATABSE_TYPE_MYSQL.equals(dbType) || DATABSE_TYPE_MARIADB.equals(dbType)) {
//			sql = MessageFormat.format(MYSQL_SQL, sqlParam);
//		} else if (DATABSE_TYPE_POSTGRE.equals(dbType)) {
//			sql = MessageFormat.format(POSTGRE_SQL, sqlParam);
//		} else {
//			int beginIndex = (page - 1) * rows;
//			int endIndex = beginIndex + rows;
//			sqlParam[2] = Integer.toString(beginIndex);
//			sqlParam[1] = Integer.toString(endIndex);
//			if (DATABSE_TYPE_ORACLE.equals(dbType) || DATABSE_TYPE_DM.equals(dbType)) {
//				sql = MessageFormat.format(ORACLE_SQL, sqlParam);
//			} else if (DATABSE_TYPE_SQLSERVER.equals(dbType)) {
//				sqlParam[0] = sql.substring(getAfterSelectInsertPoint(sql));
//				sql = MessageFormat.format(SQLSERVER_SQL, sqlParam);
//			}else{
//				//TODO 不兼容的数据库，SQL不分页
//				return sql;
//			}
//		}
//		return sql;
//	}

	/**
	 * 
	 * @param sql
	 * @return
	 */
	private static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
	}

	/**
	 * 返回首字母变为小写的字符串
	 * 
	 * @param name
	 * @return
	 */
	public static String getFirstSmall(String name) {
		name = name.trim();
		if (name.length() >= 2) {
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		} else {
			return name.toLowerCase();
		}

	}

	/**
	 * 根据SQL_URL读取SQL文件内容
	 * 
	 * @param sqlurl
	 * @return
	 */
	public static String getMethodSqlLogicJar(String sqlurl) {
		StringBuffer sb = new StringBuffer();
		// 返回读取指定资源的输入流
		InputStream is = MiniDaoUtil.class.getResourceAsStream(sqlurl);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = "";
		try {
			while ((s = br.readLine()) != null)
				sb.append(s + " ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 判断方法是否是抽象方法
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isAbstract(Method method) {
		int mod = method.getModifiers();
		return Modifier.isAbstract(mod);
	}

	/**
	 * 判断Class是否是基本包装类型
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isWrapClass(Class<?> clz) {
		try {
			return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		logger.debug(isWrapClass(Long.class));
		logger.debug(isWrapClass(Integer.class));
		logger.debug(isWrapClass(String.class));
	}
}
