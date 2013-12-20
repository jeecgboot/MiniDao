/**
 * 
 */
package org.jeecgframework.minidao.spring.rowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.jeecgframework.minidao.util.CamelCaseUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * @author fancq
 *
 */
public class GenericRowMapper<T> implements RowMapper<T> {
	private Class<T> clazz;
	
	public GenericRowMapper(Class<T> claz) {
		this.clazz = claz;
	}
	
	public T mapRow(ResultSet resultset, int rowNum) throws SQLException {
		try {
			ResultSetMetaData rsmd = resultset.getMetaData();
			int columnCount = rsmd.getColumnCount();
			T bean = clazz.newInstance();
			ConvertUtils.register(new org.jeecgframework.minidao.util.DateConverter(), Date.class);
			ConvertUtils.register(new org.jeecgframework.minidao.util.BigDecimalConverter(), BigDecimal.class);
			ConvertUtils.register(new org.jeecgframework.minidao.util.IntegerConverter(), Integer.class);
			for (int i = 1; i <= columnCount; i++) {
				String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
				Object obj = getColumnValue(resultset, i);
				String camelKey = CamelCaseUtils.toCamelCase(key);
				BeanUtils.setProperty(bean, camelKey, obj);
			}
			return bean;
		}
		// Misplaced declaration of an exception variable
		catch (Exception e) {
			throw new SQLException("mapRow error.", e);
		}
	}

	protected String getColumnKey(String columnName) {
		return columnName;
	}

	protected Object getColumnValue(ResultSet rs, int index)
			throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}