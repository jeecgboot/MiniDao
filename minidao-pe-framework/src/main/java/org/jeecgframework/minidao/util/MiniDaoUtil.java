package org.jeecgframework.minidao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

/**
 * 
 * @Title:JdkLocalUtil
 * @description:JdkLocalUtil
 * @author 张代浩
 * @date Jul 5, 2013 2:58:29 PM
 * @version V1.0
 */
public class MiniDaoUtil {
	/**
	 * 数据库类型
	 */
	public static final String DATABSE_TYPE_MYSQL = "mysql";
	public static final String DATABSE_TYPE_POSTGRE = "postgresql";
	public static final String DATABSE_TYPE_ORACLE = "oracle";
	public static final String DATABSE_TYPE_SQLSERVER = "sqlserver";
	/**
	 * 分页SQL
	 */
	public static final String MYSQL_SQL = "select * from ( {0}) sel_tab00 limit {1},{2}"; // mysql
	public static final String POSTGRE_SQL = "select * from ( {0}) sel_tab00 limit {2} offset {1}";// postgresql
	public static final String ORACLE_SQL = "select * from (select row_.*,rownum rownum_ from ({0}) row_ where rownum <= {1}) where rownum_>{2}"; // oracle
	public static final String SQLSERVER_SQL = "select * from ( select row_number() over(order by tempColumn) tempRowNumber, * from (select top {1} tempColumn = 0, {0}) t ) tt where tempRowNumber > {2}"; // sqlserver

	private static final Logger logger = Logger.getLogger(MiniDaoUtil.class);

	/**
	 * 按照数据库类型，封装SQL
	 * 
	 * @param dbType
	 *            数据库类型
	 * @param sql
	 * @param page
	 * @param rows
	 * @return
	 */
	public static String createPageSql(String dbType, String sql, int page,
			int rows) {
		int beginNum = (page - 1) * rows;
		String[] sqlParam = new String[3];
		sqlParam[0] = sql;
		sqlParam[1] = beginNum + "";
		sqlParam[2] = rows + "";
		String jdbcType = dbType;
		if (jdbcType == null || "".equals(jdbcType)) {
			throw new RuntimeException(
					"org.jeecgframework.minidao.aop.MiniDaoHandler:(数据库类型:dbType)没有设置,请检查配置文件");
		}
		if (jdbcType.indexOf(DATABSE_TYPE_MYSQL) != -1) {
			sql = MessageFormat.format(MYSQL_SQL, sqlParam);
		} else if (jdbcType.indexOf(DATABSE_TYPE_POSTGRE) != -1) {
			sql = MessageFormat.format(POSTGRE_SQL, sqlParam);
		} else {
			int beginIndex = (page - 1) * rows;
			int endIndex = beginIndex + rows;
			sqlParam[2] = Integer.toString(beginIndex);
			sqlParam[1] = Integer.toString(endIndex);
			if (jdbcType.indexOf(DATABSE_TYPE_ORACLE) != -1) {
				sql = MessageFormat.format(ORACLE_SQL, sqlParam);
			} else if (jdbcType.indexOf(DATABSE_TYPE_SQLSERVER) != -1) {
				sqlParam[0] = sql.substring(getAfterSelectInsertPoint(sql));
				sql = MessageFormat.format(SQLSERVER_SQL, sqlParam);
			}
		}
		return sql;
	}

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
