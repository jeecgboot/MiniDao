package org.jeecgframework.minidao.spring.rowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 使用默认的key作为关键字
 * 
 * @author JueYue
 * @date 2013-9-27
 * @version 1.0
 */
public class MiniColumnOriginalMapRowMapper implements
		RowMapper<Map<String, Object>> {

	public MiniColumnOriginalMapRowMapper() {
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> createColumnMap(int columnCount) {
		return new LinkedMap(columnCount);
	}

	protected String getColumnKey(String columnName) {
		return columnName;
	}

	protected Object getColumnValue(ResultSet rs, int index)
			throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

	public Map<String, Object> mapRow(ResultSet resultset, int rowNum)
			throws SQLException {
		ResultSetMetaData rsmd = resultset.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String, Object> mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			Object obj = getColumnValue(resultset, i);
			mapOfColValues.put(key, obj);
		}

		return mapOfColValues;
	}

}
