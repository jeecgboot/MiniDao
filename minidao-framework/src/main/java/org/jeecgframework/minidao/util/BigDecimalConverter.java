package org.jeecgframework.minidao.util;

import java.math.BigDecimal;

import org.apache.commons.beanutils.Converter;

/**
 * @author fancq
 *
 */
public class BigDecimalConverter implements Converter {
	@Override
	public Object convert(Class type, Object value) {
		if (value == null)
			return null;
		if (value instanceof String) {
			String tmp = (String) value;
			if (tmp.trim().length() == 0) {
				return null;
			} else {
				return new BigDecimal(tmp);
			}
		}
		return null;
	}
}
