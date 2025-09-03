//package test;
//
//import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
//import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor49;
//import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * 测试 MiniDaoUtil.parseSqlFields 的字段解析能力，覆盖多种 SQL 形态。
// * for [QQYUN-13476]online 报表SqlServer兼容改造完
// * @Author: chenrui
// * @Date: 2025-08-20 10:30
// */
//public class ParseSqlFieldsTest {
//
//    AbstractSqlProcessor sqlProcessor;
//
//    @Before
//    public void setUp() {
//        // 初始化 SQL 解析器
//        sqlProcessor = new JsqlparserSqlProcessor49();
////        sqlProcessor = new SimpleSqlProcessor();
//    }
//
//
//    // 统一的日志输出：打印测试名称、目的、原始 SQL、解析字段，并以分隔线区分用例
//    private static void logCase(String testName, String purpose, String sql, Map<String,String> fields) {
//        System.out.println();
//        System.out.println("====================[ParseSqlFieldsTest#" + testName + "]====================");
//        System.out.println("目的: " + purpose);
//        System.out.println("原始SQL: " + sql);
//        System.out.println("解析字段: \n" + (fields.isEmpty() ? "(empty)" : fields.entrySet().stream().map(entry -> entry.getKey() + " -> " + entry.getValue()).collect(Collectors.joining("\n"))));
//        System.out.println("========================================================================\n");
//    }
//
//    /**
//     * 最基础的查询（无别名）
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testBasicSelectNoAlias() {
//        String sql = "SELECT id, username, age FROM sys_user";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testBasicSelectNoAlias", "基础查询（无别名）字段解析", sql, fields);
//        Assert.assertTrue(keys.contains("id"));
//        Assert.assertTrue(keys.contains("username"));
//        Assert.assertTrue(keys.contains("age"));
//    }
//
//    /**
//     * 最基础的查询（表有别名）
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testBasicSelectWithAlias() {
//        String sql = "SELECT u.id, u.username, u.age FROM sys_user u";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testBasicSelectWithAlias", "基础查询（表有别名）字段解析", sql, fields);
//        Assert.assertTrue(keys.contains("id"));
//        Assert.assertTrue(keys.contains("username"));
//        Assert.assertTrue(keys.contains("age"));
//    }
//
//    /**
//     * 联表查询
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testJoinQuery() {
//        String sql = "SELECT u.id, d.name AS dept_name, u.username FROM sys_user u JOIN sys_dept d ON u.dept_id = d.id";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testJoinQuery", "联表查询字段解析，别名应生效", sql, fields);
//        Assert.assertTrue(keys.contains("id"));
//        Assert.assertTrue(keys.contains("dept_name")); // 别名应生效
//        Assert.assertTrue(keys.contains("username"));
//    }
//
//    /**
//     * 子查询（from 子句中的子查询）
//     * 期望：外层别名字段与内层别名字段均可被解析（返回的 list 里通常包含两份 map）。
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testSubqueryInFrom() {
//        String sql = "select u.name1 as name2 from (select username as name1 from sys_user) u";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testSubqueryInFrom", "FROM 子查询：解析内外层别名字段", sql, fields);
//        Assert.assertTrue(keys.contains("name2"));
//    }
//
//    /**
//     * 字段子查询（select 项内的子查询）
//     * 期望：顶层字段包含普通字段与子查询的别名字段。
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testFieldSubquery() {
//        String sql = "select id, (select username as name1 from sys_user u2 where u1.id = u2.id) as name2 from sys_user u1";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testFieldSubquery", "字段子查询：解析子查询别名字段", sql, fields);
//        Assert.assertTrue(keys.contains("id"));
//        Assert.assertTrue(keys.contains("name2")); // 子查询别名作为字段名
//    }
//
//    /**
//     * 字段中包含函数（含别名与不含别名）
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testFunctionFields() {
//        // 含别名
//        String sql1 = "select max(sex) as max_sex, id from sys_user";
//        Map<String,String> fields1 = sqlProcessor.parseSelectAliasMap(sql1);
//        Set<String> k1 = fields1.keySet();
//        logCase("testFunctionFields#1", "函数字段解析（场景1：带别名）", sql1, fields1);
//        Assert.assertTrue(k1.contains("max_sex"));
//        Assert.assertTrue(k1.contains("id"));
//
//        // 不含别名时，函数名会被当作字段文本
//        String sql2 = "select MAX(sex), id from sys_user";
//        Map<String,String> fields2 = sqlProcessor.parseSelectAliasMap(sql2);
//        Set<String> k2 = fields2.keySet();
//        logCase("testFunctionFields#2", "函数字段解析（场景2：不带别名）", sql2, fields2);
//        // 兼容大小写，判断包含 "MAX(sex)" 或 "max(sex)"
//        boolean hasFunc = k2.stream().anyMatch(s -> s.equals("MAX(sex)") || s.equals("max(sex)"));
//        Assert.assertTrue(hasFunc);
//        Assert.assertTrue(k2.contains("id"));
//    }
//
//    /**
//     * 其他类型（常量、日期函数、通配列）
//     * - 常量带别名
//     * - 日期/时间函数带别名
//     * - 通配列：u.* 应该被保留；但 * 会被排除
//     *
//     * @author chenrui
//     * @date 2025/08/20 10:30
//     */
//    @Test
//    public void testOtherTypes() {
//        String sql = "select 'aaa' as label, 123 as num, DATE(create_time) as d, u.* from sys_user u";
//        Map<String,String> fields = sqlProcessor.parseSelectAliasMap(sql);
//        Set<String> keys = fields.keySet();
//        logCase("testOtherTypes#1", "常量/日期函数/通配列解析；保留 u.*，排除 *（场景1）", sql, fields);
//        Assert.assertTrue(keys.contains("label"));
//        Assert.assertTrue(keys.contains("num"));
//        Assert.assertTrue(keys.contains("d"));
//        // u.* 会作为一个整体添加
//        Assert.assertTrue(keys.contains("u.*"));
//
//        // 纯 * 会被剔除（map 中不应包含 "*")
//        String sql2 = "select * from sys_user";
//        Map<String,String> fields2 = sqlProcessor.parseSelectAliasMap(sql2);
//        Set<String> keys2 = fields.keySet();
//        logCase("testOtherTypes#2", "通配 * 剔除验证（场景2）", sql2, fields);
//        Assert.assertFalse(keys2.contains("*"));
//    }
//}
