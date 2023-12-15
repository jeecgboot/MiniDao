package jeecg.aop;

import org.jeecgframework.minidao.aspect.EmptyInterceptor;
import org.jeecgframework.p3.core.author.LoginUser;
import org.jeecgframework.p3.core.util.plugin.ContextHolderUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * minidao拦截器实现【自动填充：创建人，创建时间，修改人，修改时间】
 */

@Service
public class MinidaoInterceptor implements EmptyInterceptor {

	/**
	 * 支付窗账号ID，保存用户Session会话中
	 */
	public static final String ALIPAY_ACCOUNT_ID = "ALIPAY_ACCOUNT_ID";
	
	@Override
	public boolean onInsert(Field[] fields, Object obj) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int j = 0; j < fields.length; j++) {
			fields[j].setAccessible(true);
			String fieldName = fields[j].getName();
			//获取登录用户
			LoginUser loginUser = ContextHolderUtils.getLoginSessionUser();
			if(loginUser!=null){
				if ("createBy".equals(fieldName)) {
					map.put("createBy", loginUser.getUserName());
				}
			}
			if ("createDate".equals(fieldName)) {
				map.put("createDate", new Date());
			}
			
			//营销平台，支付窗拦截器，注入当前在线公众ID
			if ("accountid".equals(fieldName)) {
				try {
					//Object accountid = ContextHolderUtils.getSession().get.getAttribute(ALIPAY_ACCOUNT_ID);
					Object accountid = null;
					if(accountid!=null){
						map.put("accountid", accountid);
					}
				} catch (Exception e) {
					
				}
				
			}
		}
		try {
			//回写Value值
			setFieldValue(map, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onUpdate(Field[] fields, Object obj) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int j = 0; j < fields.length; j++) {
			fields[j].setAccessible(true);
			String fieldName = fields[j].getName();
			//获取登录用户
			LoginUser loginUser = ContextHolderUtils.getLoginSessionUser();
			if(loginUser!=null){
				if ("updateBy".equals(fieldName)) {
					map.put("updateBy", loginUser.getUserName());
				}
			}
			if ("updateDate".equals(fieldName)) {
				map.put("updateDate", new Date());
			}
		}
		try {
			//回写Value值
			setFieldValue(map, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onSelect(Field[] fields, Object obj) {
		return false;
	}

	/**
	 * 设置bean 属性值
	 * 
	 * @param map
	 * @param bean
	 * @throws Exception
	 */
	private static void setFieldValue(Map<Object, Object> map, Object bean) throws Exception {
		Class<?> cls = bean.getClass();
		Method methods[] = cls.getDeclaredMethods();
		Field fields[] = cls.getDeclaredFields();

		for (Field field : fields) {
			String fldtype = field.getType().getSimpleName();
			String fldSetName = field.getName();
			String setMethod = pareSetName(fldSetName);
			if (!checkMethod(methods, setMethod)) {
				continue;
			}
			if(!map.containsKey(fldSetName)){continue;}
			Object value = map.get(fldSetName);
			Method method = cls.getMethod(setMethod, field.getType());
			if (null != value) {
				if ("String".equals(fldtype)) {
					method.invoke(bean, (String) value);
				} else if ("Double".equals(fldtype)) {
					method.invoke(bean, (Double) value);
				} else if ("int".equals(fldtype)) {
					int val = Integer.valueOf((String) value);
					method.invoke(bean, val);
				}else{
					method.invoke(bean, value);
				}
			}

		}
	}

	/**
	 * 拼接某属性set 方法
	 * 
	 * @param fldname
	 * @return
	 */
	private static String pareSetName(String fldname) {
		if (null == fldname || "".equals(fldname)) {
			return null;
		}
		String pro = "set" + fldname.substring(0, 1).toUpperCase() + fldname.substring(1);
		return pro;
	}

	/**
	 * 判断该方法是否存在
	 * 
	 * @param methods
	 * @param met
	 * @return
	 */
	private static boolean checkMethod(Method methods[], String met) {
		if (null != methods) {
			for (Method method : methods) {
				if (met.equals(method.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
