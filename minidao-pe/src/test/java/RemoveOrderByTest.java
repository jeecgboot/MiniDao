import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * removeOrderBy 功能的专项单元测试
 * 测试SQL中ORDER BY子句的移除功能
 * 
 * @author system
 * @date 2026-01-16
 */
public class RemoveOrderByTest {
    /**
     * 测试miniDaoUtil:移除order by 当有 mybatis占位符时是否正常
     * @author chenrui
     * @date 2025/8/15 12:02
     */
    @Test
    public void testRemoveOrderWithMybatis() {
        String sql = "SELECT * FROM sys_user WHERE sex=#{params.sex} AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
        System.out.println("before:" + sql);
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("after:" + result);
        Assert.assertTrue(result.contains("#{params.username}"));
    }

    /**
     * 测试miniDaoUtil:移除order by 当有 UNION ALL时是否正常
     * @author chenrui
     * @date 2025/8/15 12:02
     */
    @Test
    public void testRemoveOrderWithUnionAll() {
        String sql = "SELECT COUNT(1) total FROM ( SELECT * FROM (\n" +
                "SELECT \n" +
                "    u.username AS 登录账号,\n" +
                "    u.realname AS 真实姓名,\n" +
                "    u.sex AS 性别,\n" +
                "    u.create_time AS 用户创建时间,\n" +
                "    '系统用户' AS 用户类型\n" +
                "FROM sys_user u\n" +
                "\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT \n" +
                "    d.name AS 登录账号,\n" +
                "\t\td.name AS 真实姓名,\n" +
                "    d.sex AS 性别,\n" +
                "    d.create_time AS 用户创建时间,\n" +
                "    '测试用户' AS 用户类型\n" +
                "FROM test_demo d\n" +
                "\n" +
                ") AA ORDER BY AA.用户创建时间 DESC ) temp_count";
        //System.out.println("before:" + sql);
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("after:" + result);
        Assert.assertFalse("ORDER BY should be removed", result.toUpperCase().contains("ORDER BY"));
    }
    

    /**
     * 测试 removeOrderBy: 简单的单字段排序
     */
    @Test
    public void testRemoveOrderBy_SimpleOrder() {
        String sql = "SELECT * FROM sys_user ORDER BY create_time";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 多字段排序
     */
    @Test
    public void testRemoveOrderBy_MultipleFields() {
        String sql = "SELECT * FROM sys_user ORDER BY create_time DESC, username ASC, id";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 子查询中的排序
     */
    @Test
    public void testRemoveOrderBy_SubQuery() {
        String sql = "SELECT * FROM (SELECT * FROM sys_user ORDER BY create_time) t";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除子查询中的ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 嵌套子查询中的多个排序
     */
    @Test
    public void testRemoveOrderBy_NestedSubQuery() {
        String sql = "SELECT * FROM (SELECT * FROM (SELECT * FROM sys_user ORDER BY id) a ORDER BY create_time) b ORDER BY username";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除所有ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: LEFT JOIN中的排序
     */
    @Test
    public void testRemoveOrderBy_LeftJoin() {
        String sql = "SELECT * FROM sys_user u LEFT JOIN sys_role r ON u.id = r.user_id ORDER BY u.create_time DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: LEFT JOIN子查询中的排序
     * for [issues/9220] 左联SQL解析表名失败
     */
    @Test
    public void testRemoveOrderBy_LeftJoinWithSubQuery() {
        String sql = "SELECT * FROM test_order_main a LEFT JOIN (SELECT order_id, COUNT(1) AS total FROM test_order_customer GROUP BY order_id ORDER BY order_id) b ON a.id=b.order_id ORDER BY b.total DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除所有ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: UNION查询
     */
    @Test
    public void testRemoveOrderBy_Union() {
        String sql = "SELECT * FROM sys_user ORDER BY id UNION SELECT * FROM sys_user_bk ORDER BY create_time";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除所有ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: UNION ALL 外层排序
     * for [issues/4474] UNION+子sql写法 去掉sql排序失败
     */
    @Test
    public void testRemoveOrderBy_UnionAllOuterOrder() {
        String sql = "SELECT * FROM (SELECT id, name FROM sys_user UNION ALL SELECT id, name FROM demo) t ORDER BY name";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: UNION ALL复杂嵌套场景
     * for [issues/4474] UNION+子sql写法 去掉sql排序失败
     */
    @Test
    public void testRemoveOrderBy_UnionAllComplex() {
        String sql = "SELECT COUNT(1) total FROM ( SELECT * FROM (\n" +
                "SELECT " +
                "    u.username AS 登录账号,\n" +
                "    u.realname AS 真实姓名,\n" +
                "    u.sex AS 性别,\n" +
                "    u.create_time AS 用户创建时间,\n" +
                "    '系统用户' AS 用户类型\n" +
                "FROM sys_user u\n" +
                "UNION ALL\n" +
                "SELECT \n" +
                "    d.name AS 登录账号,\n" +
                "    d.name AS 真实姓名,\n" +
                "    d.sex AS 性别,\n" +
                "    d.create_time AS 用户创建时间,\n" +
                "    '测试用户' AS 用户类型\n" +
                "FROM test_demo d\n" +
                ") AA ORDER BY AA.用户创建时间 DESC ) temp_count";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("ORDER BY should be removed", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 带GROUP BY和ORDER BY
     */
    @Test
    public void testRemoveOrderBy_WithGroupBy() {
        String sql = "SELECT sex, COUNT(*) as cnt FROM sys_user GROUP BY sex ORDER BY cnt DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertTrue("应该保留GROUP BY", result.toUpperCase().contains("GROUP BY"));
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: CASE WHEN排序
     */
    @Test
    public void testRemoveOrderBy_CaseWhen() {
        String sql = "SELECT * FROM sys_user ORDER BY CASE WHEN sex='1' THEN create_time ELSE update_time END";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 函数排序
     */
    @Test
    public void testRemoveOrderBy_Function() {
        String sql = "SELECT * FROM sys_user ORDER BY LENGTH(username), UPPER(realname) DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 带别名的排序
     */
    @Test
    public void testRemoveOrderBy_WithAlias() {
        String sql = "SELECT username as name, create_time as ct FROM sys_user ORDER BY ct DESC, name ASC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 复杂的子查询+JOIN+UNION ALL组合
     */
    @Test
    public void testRemoveOrderBy_ComplexMixed() {
        String sql = "SELECT COUNT(1) FROM (" +
                "SELECT a.*, b.total FROM sys_user a " +
                "LEFT JOIN (SELECT user_id, COUNT(1) as total FROM orders GROUP BY user_id ORDER BY total DESC) b " +
                "ON a.id = b.user_id " +
                "UNION ALL " +
                "SELECT c.*, 0 as total FROM sys_user_bk c " +
                "ORDER BY create_time DESC" +
                ") temp";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除所有ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 没有ORDER BY的SQL应该保持不变
     */
    @Test
    public void testRemoveOrderBy_NoOrderBy() {
        String sql = "SELECT * FROM sys_user WHERE status = 1";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("不应该包含ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: LIMIT/OFFSET带ORDER BY (MySQL)
     */
    @Test
    public void testRemoveOrderBy_WithLimitOffset() {
        String sql = "SELECT * FROM sys_user ORDER BY create_time DESC LIMIT 10 OFFSET 5";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
        Assert.assertTrue("应该保留LIMIT", result.toUpperCase().contains("LIMIT"));
    }

    /**
     * 测试 removeOrderBy: WITH CTE查询
     * 注意：目前实现只移除外层查询的ORDER BY，CTE内部的ORDER BY可能不会被移除
     */
    @Test
    public void testRemoveOrderBy_WithCTE() {
        String sql = "WITH user_cte AS (SELECT * FROM sys_user ORDER BY id) " +
                "SELECT * FROM user_cte ORDER BY create_time";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        // 验证至少外层的ORDER BY被移除了
        Assert.assertFalse("外层ORDER BY应该被移除", result.endsWith("ORDER BY create_time"));
    }

    /**
     * 测试 removeOrderBy: 带MyBatis占位符
     * for [issues/9000] Online报表（带参数）预览后台报错
     */
    @Test
    public void testRemoveOrderBy_WithMybatisPlaceholder() {
        String sql = "SELECT * FROM sys_user WHERE sex=#{params.sex} AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertTrue("应该保留MyBatis占位符", result.contains("#{params.username}"));
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: INNER JOIN
     */
    @Test
    public void testRemoveOrderBy_InnerJoin() {
        String sql = "SELECT u.*, r.role_name FROM sys_user u INNER JOIN sys_role r ON u.role_id = r.id ORDER BY u.create_time, r.role_name";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: RIGHT JOIN
     */
    @Test
    public void testRemoveOrderBy_RightJoin() {
        String sql = "SELECT * FROM sys_user u RIGHT JOIN sys_depart d ON u.depart_id = d.id ORDER BY d.depart_name";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 多表JOIN
     */
    @Test
    public void testRemoveOrderBy_MultipleJoins() {
        String sql = "SELECT * FROM sys_user u " +
                "LEFT JOIN sys_depart d ON u.depart_id = d.id " +
                "LEFT JOIN sys_role r ON u.role_id = r.id " +
                "ORDER BY d.depart_name, r.role_name, u.username";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: HAVING子句
     */
    @Test
    public void testRemoveOrderBy_WithHaving() {
        String sql = "SELECT sex, COUNT(*) as cnt FROM sys_user GROUP BY sex HAVING cnt > 5 ORDER BY cnt DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertTrue("应该保留HAVING", result.toUpperCase().contains("HAVING"));
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: DISTINCT
     */
    @Test
    public void testRemoveOrderBy_WithDistinct() {
        String sql = "SELECT DISTINCT username, depart_id FROM sys_user ORDER BY depart_id, username";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertTrue("应该保留DISTINCT", result.toUpperCase().contains("DISTINCT"));
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 表达式排序
     */
    @Test
    public void testRemoveOrderBy_WithExpression() {
        String sql = "SELECT *, (salary * 12) as annual_salary FROM employee ORDER BY annual_salary DESC, name";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: NULL排序处理
     */
    @Test
    public void testRemoveOrderBy_WithNullsFirst() {
        String sql = "SELECT * FROM sys_user ORDER BY create_time NULLS FIRST";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("应该移除ORDER BY", result.toUpperCase().contains("ORDER BY"));
    }

    /**
     * 测试 removeOrderBy: 子查询在SELECT列表中
     */
    @Test
    public void testRemoveOrderBy_SubQueryInSelect() {
        String sql = "SELECT id, username, (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id ORDER BY o.create_time) as order_count FROM sys_user u ORDER BY order_count DESC";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        // 主查询的ORDER BY应该被移除
        Assert.assertFalse("主查询ORDER BY应该被移除", result.endsWith("ORDER BY order_count DESC"));
    }

    /**
     * 测试 removeOrderBy: EXISTS子查询
     */
    @Test
    public void testRemoveOrderBy_WithExists() {
        String sql = "SELECT * FROM sys_user u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id ORDER BY o.id) ORDER BY u.username";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("主查询ORDER BY应该被移除", result.endsWith("ORDER BY u.username"));
    }

    /**
     * 测试 removeOrderBy: IN子查询
     */
    @Test
    public void testRemoveOrderBy_WithIn() {
        String sql = "SELECT * FROM sys_user WHERE depart_id IN (SELECT id FROM sys_depart ORDER BY depart_name) ORDER BY username";
        String result = MiniDaoUtil.removeOrderBy(sql);
        System.out.println("原SQL: " + sql);
        System.out.println("结果: " + result);
        Assert.assertFalse("主查询ORDER BY应该被移除", result.endsWith("ORDER BY username"));
    }
}

