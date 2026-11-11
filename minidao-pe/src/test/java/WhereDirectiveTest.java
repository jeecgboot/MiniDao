import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jeecgframework.minidao.util.FreemarkerParseFactory;
import org.jeecgframework.minidao.util.WhereDirective;
import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * SQL 模板标签 <@where>：对标 MyBatis 的 <where>，有条件才输出 where，并去掉开头多余的 and / or
 */
public class WhereDirectiveTest {

    private static final String LIST_SQL = "select * from employee <@where>"
            + "<#if name??> and name = :name</#if>"
            + "<#if age??> and age = :age</#if>"
            + "</@where> order by id";

    @Test
    public void noConditionOutputsNoWhere() {
        assertEquals("select * from employee order by id", parse(LIST_SQL, params()));
    }

    @Test
    public void leadingAndIsRemoved() {
        assertEquals("select * from employee where name = :name order by id", parse(LIST_SQL, params("name", "张三")));
    }

    @Test
    public void innerAndIsKept() {
        assertEquals("select * from employee where name = :name and age = :age order by id",
                parse(LIST_SQL, params("name", "张三", "age", 18)));
    }

    @Test
    public void leadingOrAndUpperCaseAreRemoved() {
        assertEquals("select * from employee where name = :name", parse("select * from employee <@where> OR name = :name</@where>", params()));
        assertEquals("select * from employee where name = :name", parse("select * from employee <@where>AND name = :name</@where>", params()));
        assertEquals("select * from employee where (a = 1 or b = 2)", parse("select * from employee <@where>and(a = 1 or b = 2)</@where>", params()));
    }

    @Test
    public void columnStartingWithAndOrIsNotTouched() {
        assertEquals("select * from employee where android_id = :id", parse("select * from employee <@where>android_id = :id</@where>", params()));
        assertEquals("select * from employee where order_no = :no", parse("select * from employee <@where>order_no = :no</@where>", params()));
    }

    @Test
    public void commentOnlyBodyCountsAsEmpty() {
        assertEquals("select * from employee", parse("select * from employee <@where> /* 名称 */ </@where>", params()));
        assertEquals("select * from employee where name = :name",
                parse("select * from employee <@where>/* 名称 */ and name = :name</@where>", params()));
    }

    @Test
    public void worksInsideSubQuery() {
        String sql = "select * from (select * from employee <@where><#if name??> and name = :name</#if></@where>) t";
        assertEquals("select * from (select * from employee ) t", parse(sql, params()));
        assertEquals("select * from (select * from employee where name = :name ) t", parse(sql, params("name", "张三")));
    }

    @Test
    public void fileTemplateWithIncludeWorks() {
        String tpl = "whereDirective/WhereDirectiveTest_list.sql";
        assertEquals("select * from employee order by id",
                parseFile(tpl, params("employee", new HashMap<String, Object>())));
        Map<String, Object> employee = params("name", "张三");
        assertEquals("select * from employee where name = :employee.name order by id",
                parseFile(tpl, params("employee", employee)));
    }

    @Test
    public void legacyWhereOneEqualsOneIsUnchanged() {
        assertEquals("select * from employee where 1=1 and name = :name",
                parse("select * from employee where 1=1 <#if name??> and name = :name</#if>", params("name", "张三")));
    }

    @Test
    public void parametersAreRejected() throws Exception {
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        cfg.setSharedVariable("where", new WhereDirective());
        Template template = new Template("t", "select 1 <@where prefix='x'> and a = 1</@where>", cfg);
        try {
            template.process(new HashMap<String, Object>(), new StringWriter());
            fail("<@where> 不应接受参数");
        } catch (TemplateException e) {
            System.out.println("模板：" + template);
            System.out.println("报错：" + e.getMessageWithoutStackTop());
            System.out.println();
            assertTrue(e.getMessage(), e.getMessage().contains("<@where>"));
        }
    }

    /**
     * 用 @Sql 注解同一套解析（字符串模板）渲染 SQL，并打印模板、参数和结果
     * @param sql 模板
     * @param params 参数
     * @return 渲染并清理后的 SQL
     */
    private static String parse(String sql, Map<String, Object> params) {
        String result = FreemarkerParseFactory.parseTemplateContent(sql, params);
        print(sql, params, result);
        return result;
    }

    /**
     * 用 *.sql 文件模板渲染 SQL，并打印模板、参数和结果
     * @param tplName 模板路径
     * @param params 参数
     * @return 渲染并清理后的 SQL
     */
    private static String parseFile(String tplName, Map<String, Object> params) {
        String result = FreemarkerParseFactory.parseTemplate(tplName, params);
        print(tplName, params, result);
        return result;
    }

    /**
     * 打印一次解析的输入和输出
     * @param template 模板内容或模板路径
     * @param params 参数
     * @param result 解析结果
     */
    private static void print(String template, Map<String, Object> params, String result) {
        System.out.println("模板：" + template);
        System.out.println("参数：" + params);
        System.out.println("结果：" + result);
        System.out.println();
    }

    /**
     * 组装参数
     * @param kv 依次为参数名、参数值
     * @return 参数 map
     */
    private static Map<String, Object> params(Object... kv) {
        Map<String, Object> map = new HashMap<>(8);
        for (int i = 0; i < kv.length; i += 2) {
            map.put((String) kv[i], kv[i + 1]);
        }
        return map;
    }
}
