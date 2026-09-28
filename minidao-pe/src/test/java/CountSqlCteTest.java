import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * CTE表达式 count SQL 解析的单元测试
 * <p>
 * 针对 issues/4798：CTE表达式在分页查询模式下报错 java.lang.NoSuchMethodError
 *
 * @author system
 * @date 2026-09-23
 */
public class CountSqlCteTest {

    /**
     * 测试带CTE的SQL生成count语句（分页查询会调用）
     */
    @Test
    public void testCountSqlWithCte() {
        String sql = "WITH cte_result AS (SELECT * FROM test_table LIMIT 1) SELECT * FROM test_table LIMIT 1";
        String countSql = MiniDaoUtil.getCountSql(sql);
        System.out.println("countSql:" + countSql);
        Assert.assertNotNull(countSql);
        // count语句应保留CTE定义
        Assert.assertTrue(countSql.contains("WITH cte_result"));
        Assert.assertTrue(countSql.contains("count"));
    }

    /**
     * 测试带CTE且CTE内有order by的SQL生成count语句（order by应被移除）
     */
    @Test
    public void testCountSqlWithCteOrderBy() {
        String sql = "WITH cte AS (SELECT id, name FROM test_table WHERE id > 10 ORDER BY id DESC) SELECT id FROM cte ORDER BY name";
        String countSql = MiniDaoUtil.getCountSql(sql);
        System.out.println("countSql:" + countSql);
        Assert.assertNotNull(countSql);
        Assert.assertTrue(countSql.contains("WITH cte"));
        Assert.assertTrue(countSql.contains("count"));
    }
}
