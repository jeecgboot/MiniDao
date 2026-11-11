package org.jeecgframework.minidao.util;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SQL 模板标签 &lt;@where&gt;，对标 MyBatis 的 &lt;where&gt;：
 * 标签里没有条件（只有空白或注释）时什么都不输出；有条件时去掉开头多余的 and / or，前面补上 where。
 * <pre>
 * select * from employee
 * &lt;@where&gt;
 *   &lt;#if name??&gt; and name = :name &lt;/#if&gt;
 *   &lt;#if age??&gt; and age = :age &lt;/#if&gt;
 * &lt;/@where&gt;
 * order by id
 * </pre>
 *
 * @author wangshuai
 * @date 2026-11-10
 */
public class WhereDirective implements TemplateDirectiveModel {

    /**
     * 标签名，模板里写 &lt;@where&gt;
     */
    public static final String NAME = "where";

    /**
     * 注释：块注释和行首的 // 注释，与 FreemarkerParseFactory 清理 SQL 的规则一致
     */
    private static final Pattern COMMENT = Pattern.compile("(?ms)/\\*.*?\\*/|^\\s*//.*?$");

    /**
     * 开头的 and / or 关键字（android_id、order_no 这类字段名不算）
     */
    private static final Pattern LEADING_AND_OR = Pattern.compile("(?i)^(?:and|or)\\b");

    /**
     * 渲染标签内容，有条件才输出 where 子句
     * @param env 模板运行环境
     * @param params 标签参数，&lt;@where&gt; 不支持参数
     * @param loopVars 循环变量，不使用
     * @param body 标签内容
     * @throws TemplateException 传了参数时抛出
     * @throws IOException 输出失败
     */
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateModelException("<@where> 不支持参数");
        }
        if (body == null) {
            return;
        }
        StringWriter writer = new StringWriter();
        body.render(writer);
        // 1. 去掉注释（/* 名称 */ 和行首的 // 注释）再去掉首尾空白，例：" /* 名称 */ and name = :name " → "and name = :name"
        String condition = COMMENT.matcher(writer.toString()).replaceAll("").trim();
        // 2. 去掉开头的一个 and / or（不分大小写），例："and name = :name and age = :age" → "name = :name and age = :age"；
        //    只认独立的关键字，android_id、order_no 这类字段名不动
        condition = LEADING_AND_OR.matcher(condition).replaceFirst("").trim();
        // 3. 什么条件都没有（只有空白、注释）就不输出 where
        if (condition.isEmpty()) {
            return;
        }
        env.getOut().write(" where " + condition + " ");
    }
}
