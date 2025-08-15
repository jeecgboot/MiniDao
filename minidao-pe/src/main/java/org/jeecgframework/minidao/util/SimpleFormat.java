package org.jeecgframework.minidao.util;

import org.apache.commons.lang3.StringUtils;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 格式化工具
 * @since 1.0.0
 * @version 1.0.0
 *
 */
public class SimpleFormat {

	public SimpleFormat() {
	}

	/**
	 * 格式化为0.00的数字
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String number(Object obj) {
		obj = obj == null || obj.toString().length() == 0 ? 0 : obj;
		if (obj.toString().equalsIgnoreCase("NaN"))
			return "NaN";
		return new DecimalFormat("0.00").format(Double.parseDouble(obj
				.toString()));
	}

	/**
	 * 格式化为数字
	 * 
	 * @param obj
	 * @param pattern
	 * @return {@link String}
	 */
	public String number(Object obj, String pattern) {
		obj = obj == null || obj.toString().length() == 0 ? 0 : obj;
		if (obj.toString().equalsIgnoreCase("NaN"))
			return "NaN";
		return new DecimalFormat(pattern).format(Double.parseDouble(obj
				.toString()));
	}

	/**
	 * 格式化为整数
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String round(Object obj) {
		obj = obj == null || obj.toString().length() == 0 ? 0 : obj;
		if (obj.toString().equalsIgnoreCase("NaN"))
			return "NaN";
		return new DecimalFormat("0")
				.format(Double.parseDouble(obj.toString()));
	}

	/**
	 * 格式化为金额
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String currency(Object obj) {
		obj = obj == null || obj.toString().length() == 0 ? 0 : obj;
		return NumberFormat.getCurrencyInstance(Locale.CHINA).format(obj);
	}

	/**
	 * 将timestamp格式的字符转换为用户指定格式
	 * @param obj
	 * @param pattern
	 * @return {@link String}
	 * @throws Exception
	 */
	public String timestampToString(Object obj,String pattern){
		if(obj==null)return "";
		DateFormat format1 = new SimpleDateFormat("dd-MM月 -yy");
		DateFormat format2 = new SimpleDateFormat(pattern);     
		Date date=null;
		try {
			date = format1.parse(obj.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			return "error";
		}
		return format2.format(date);
	}
	
	/**
	 * 格式化为百分比
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String percent(Object obj) {
		obj = obj == null || obj.toString().length() == 0 ? 0 : obj;
		if (obj.toString().equalsIgnoreCase("NaN"))
			return "";
		return NumberFormat.getPercentInstance(Locale.CHINA).format(obj);
	}

	/**
	 * 格式化为日期
	 * 
	 * @param obj
	 * @param pattern
	 * @return {@link String}
	 */
	public String date(Object obj, String pattern) {
		if(obj==null)return "";
		return new SimpleDateFormat(pattern).format(obj);
	}

	/**
	 * 格式化为长日期
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String date(Object obj) {
		if(obj==null)return "";
		return DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA)
				.format(obj);
	}

	/**
	 * 格式化为时间
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String time(Object obj) {
		if(obj==null)return "";
		return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.CHINA)
				.format(obj);
	}

	/**
	 * 格式化为长时间
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String datetime(Object obj) {
		if(obj==null)return "";
		return DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.SHORT, Locale.CHINA).format(obj);
	}
	
	/**
	 * 格式化为in所需参数
	 * 
	 * @param obj
	 * @return {@link String}
	 */
	public String getInStrs(List<String> params) {
		StringBuffer join = new StringBuffer();
		for(String p:params){
			// SQL注入检查
			SqlInjectionUtil.check(p,null);
			join.append("'"+p+"',");
		}
		
		String result = join.toString();
		if(!"".equals(result) || result.endsWith(",")){
			result = result.substring(0,result.length() - 1);
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * 拼接字符串
	 *
	 * @param str 需要拼接的字符串
	 * @return {@link String}
	 */
	public String concat(String... str) {
		StringBuilder sb = new StringBuilder();
		for (String s : str) {
			// SQL注入检查
			SqlInjectionUtil.check(s,null);
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * in查询字符串分割
	 *
	 * @param str 需要分割的字符串
	 * @return
	 */
	public String in(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		String[] params = str.split(",");
		StringBuilder join = new StringBuilder();
		for (String p : params) {
			// SQL注入检查
			SqlInjectionUtil.check(p,null);
			
			join.append("'").append(p).append("',");
		}
		String result = join.toString();
		if (StringUtils.isNotEmpty(result) || result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
			return result;
		} else {
			return null;
		}
	}

	/**
	 * in查询字符串分割
	 *
	 * @param str 需要分割的字符串
	 * @return
	 */
	public String inNumber(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		String[] params = str.split(",");
		StringBuilder join = new StringBuilder();
		for (String p : params) {
			join.append(p).append(",");
		}
		String result = join.toString();
		if (StringUtils.isNotEmpty(result) || result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
			return result;
		} else {
			return null;
		}
	}
}
