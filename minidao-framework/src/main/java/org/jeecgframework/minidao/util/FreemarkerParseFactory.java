package org.jeecgframework.minidao.util;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 
 * @Title:FreemarkerHelper
 * @description:Freemarker引擎协助类
 * @author 赵俊夫
 * @date Jul 5, 2013 2:58:29 PM
 * @version V1.0
 */
public class FreemarkerParseFactory {
	private static final Configuration _tplConfig = new Configuration();

	public FreemarkerParseFactory() {
		_tplConfig.setClassForTemplateLoading(this.getClass(), "/");
		_tplConfig.setNumberFormat("0.#####################");  
	}
	/**
	 * 解析ftl
	 * @param tplName 模板名
	 * @param encoding 编码
	 * @param paras 参数
	 * @return
	 */
	public String parseTemplate(String tplName, String encoding,
			Map<String, Object> paras) {
		try {
			StringWriter swriter = new StringWriter();
			Template mytpl = _tplConfig.getTemplate(tplName, encoding);
			mytpl.process(paras, swriter);
			return swriter.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}

	}
	/**
	 * 解析ftl
	 * @param tplName 模板名
	 * @param encoding 编码
	 * @param paras 参数
	 * @param swriter 输出流
	 */
	public void parseTemplate(String tplName, String encoding,
			Map<String, Object> paras,FileWriter swriter) {
		try {
			Template mytpl = _tplConfig.getTemplate(tplName);
			mytpl.process(paras, swriter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解析ftl模板
	 * @param tplName 模板名
	 * @param paras 参数
	 * @return
	 */
	public String parseTemplate(String tplName, Map<String, Object> paras) {
		return this.parseTemplate(tplName, "utf-8", paras);
	}

	
	/**
	 * 解析ftl
	 * @param tplContent 模板内容
	 * @param encoding 编码
	 * @param paras 参数
	 * @return String 模板解析后内容
	 */
	public String parseTemplateContent(String tplContent,
			Map<String, Object> paras, String encoding) {
		Configuration cfg = new Configuration();    
	    StringWriter writer = new StringWriter(); 
        cfg.setTemplateLoader(new StringTemplateLoader(tplContent));  
        encoding = encoding==null?"UTF-8":encoding;
        cfg.setDefaultEncoding(encoding);    
   
        Template template;
		try {
			template = cfg.getTemplate("");
	        template.process(paras, writer);    
			} catch (Exception e) {
				e.printStackTrace();
			}
        return writer.toString();       
	}
	
	/**
	 * 解析ftl
	 * @param tplContent 模板内容
	 * @param encoding 编码
	 * @param paras 参数
	 * @return String 模板解析后内容
	 */
	public String parseTemplateContent(String tplContent,
			Map<String, Object> paras) {
		Configuration cfg = new Configuration();    
	    StringWriter writer = new StringWriter(); 
        cfg.setTemplateLoader(new StringTemplateLoader(tplContent));  
        cfg.setDefaultEncoding("UTF-8");    
   
        Template template;
		try {
			template = cfg.getTemplate("");
	        template.process(paras, writer);    
			} catch (Exception e) {
				e.printStackTrace();
			}
        return writer.toString();       
	}
}