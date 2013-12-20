package org.jeecgframework.minidao.util;

import org.apache.commons.beanutils.Converter;

/**
 * @author fancq
 *
 */
public class IntegerConverter implements Converter {
	@Override
	public Object convert(Class type, Object value) {
		if (value == null)
			return null;
		if (value instanceof String) {
			String tmp = (String) value;
			if (tmp.trim().length() == 0) {
				return null;
			} else {
				return new Integer(tmp);
			}
		}
		return null;
	}
}