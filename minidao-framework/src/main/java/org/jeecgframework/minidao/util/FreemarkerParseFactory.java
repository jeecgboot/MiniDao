package org.jeecgframework.minidao.util;

import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author 赵俊夫
 * @version V1.0
 * @Title:FreemarkerHelper
 * @description:Freemarker引擎协助类
 * @date Jul 5, 2013 2:58:29 PM
 */
public class FreemarkerParseFactory {

    private static final Logger logger = LoggerFactory
            .getLogger(FreemarkerParseFactory.class);

    private static final String ENCODE = "utf-8";
    /**
     * 文件缓存
     */
    private static final Configuration _tplConfig = new Configuration();
    /**
     * SQL 缓存
     */
    private static final Configuration _sqlConfig = new Configuration();

    private static StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();

    // 使用内嵌的(?ms)打开单行和多行模式
    private final static Pattern p = Pattern
            .compile("(?ms)/\\*.*?\\*/|^\\s*//.*?$");

    static {
        _tplConfig.setClassForTemplateLoading(
                new FreemarkerParseFactory().getClass(), "/");
        _tplConfig.setNumberFormat("0.#####################");
        _sqlConfig.setTemplateLoader(stringTemplateLoader);
        _sqlConfig.setNumberFormat("0.#####################");
    }

    /**
     * 判断模板是否存在
     */
    public static boolean isExistTemplate(String tplName) {
        try {
            Template mytpl = _tplConfig.getTemplate(tplName, "UTF-8");
            if (mytpl == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 解析ftl模板
     *
     * @param tplName 模板名
     * @param paras   参数
     * @return
     */
    public static String parseTemplate(String tplName, Map<String, Object> paras) {
        try {
            StringWriter swriter = new StringWriter();
            Template mytpl = _tplConfig.getTemplate(tplName, ENCODE);
            mytpl.process(paras, swriter);
            return getSqlText(swriter.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
            logger.error("发送一次的模板key:{}", tplName);
            throw new RuntimeException("解析SQL模板异常");
        }
    }

    /**
     * 解析ftl
     *
     * @param tplContent 模板内容
     * @param paras      参数
     * @return String 模板解析后内容
     */
    public static String parseTemplateContent(String tplContent,
                                              Map<String, Object> paras) {
        try {
            StringWriter swriter = new StringWriter();
            if (stringTemplateLoader.findTemplateSource("sql_" + tplContent.hashCode()) == null) {
                stringTemplateLoader.putTemplate("sql_" + tplContent.hashCode(), tplContent);
            }
            Template mytpl = _sqlConfig.getTemplate("sql_" + tplContent.hashCode(), ENCODE);
            mytpl.process(paras, swriter);
            return getSqlText(swriter.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
            logger.error("发送一次的模板key:{}", tplContent);
            throw new RuntimeException("解析SQL模板异常");
        }
    }

    /**
     * 除去无效字段，去掉注释 不然批量处理可能报错 去除无效的等于
     */
    private static String getSqlText(String sql) {
        // 将注释替换成""
        sql = p.matcher(sql).replaceAll("");
        sql = sql.replaceAll("\\n", " ").replaceAll("\\t", " ")
                .replaceAll("\\s{1,}", " ").trim();
        // 去掉 最后是 where这样的问题
        if (sql.endsWith("where") || sql.endsWith("where ")) {
            sql = sql.substring(0, sql.lastIndexOf("where"));
        }
        // 去掉where and 这样的问题
        int index = 0;
        while ((index = StringUtils.indexOfIgnoreCase(sql, "where and", index)) != -1) {
            sql = sql.substring(0, index + 5)
                    + sql.substring(index + 9, sql.length());
        }
        // 去掉 , where 这样的问题
        index = 0;
        while ((index = StringUtils.indexOfIgnoreCase(sql, ", where", index)) != -1) {
            sql = sql.substring(0, index)
                    + sql.substring(index + 1, sql.length());
        }
        // 去掉 最后是 ,这样的问题
        if (sql.endsWith(",") || sql.endsWith(", ")) {
            sql = sql.substring(0, sql.lastIndexOf(","));
        }
        return sql;
    }
}