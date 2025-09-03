//package test;
//
//import org.jeecgframework.minidao.pojo.MiniDaoPage;
//import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor49;
//import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
//import org.jeecgframework.minidao.util.MiniDaoUtil;
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// * @Description: sql解析单元测试
// * @Author: chenrui
// * @Date: 2024/9/27 15:46
// */
//public class SqlParserTest {
//
//    /**
//     * 测试miniDaoUtil:添加order by
//     * @author chenrui
//     * @date 2024/9/27 19:14
//     */
//    @Test
//    public void testAddOrderMiniUtil() {
//        // 复杂嵌套语句
//        String sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                "SELECT x.theme_name,(" +
//                "SELECT theme_name FROM (" +
//                "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                "GROUP BY theme_name,base_theme_name ORDER BY theme_name2 Desc,theme_name1,theme_name;";
//        System.out.println("before:" + sql);
//        String result1 = MiniDaoUtil.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                "SELECT x.theme_name,(" +
//                "SELECT theme_name FROM (" +
//                "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                "GROUP BY theme_name,base_theme_name;";
//        result1 = MiniDaoUtil.addOrderBy(sql, "base_theme_name", true);
//        // 普通语句
//        System.out.println("after1:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM ABC order By theme_name DESC";
//        System.out.println("before2:" + sql);
//        result1 = MiniDaoUtil.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after2:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM ABC";
//        System.out.println("before3:" + sql);
//        result1 = MiniDaoUtil.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after3:" + result1);
//    }
//
//
//    /**
//     * 测试jsqlParser的添加排序功能
//     * @author chenrui
//     * @date 2024/9/27 19:14
//     */
//     @Test
//     public void testAddOrderJSqlParser() {
//         JsqlparserSqlProcessor49 processor = new JsqlparserSqlProcessor49();
//         String sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                 "SELECT x.theme_name,(" +
//                 "SELECT theme_name FROM (" +
//                 "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                 "GROUP BY theme_name,base_theme_name ORDER BY theme_name2 Desc,theme_name1,theme_name;";
//         System.out.println("before:" + sql);
//         String newSql = processor.addOrderBy(sql, "base_theme_name", true);
//         System.out.println("after:" + newSql);
//         sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                 "SELECT x.theme_name,(" +
//                 "SELECT theme_name FROM (" +
//                 "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                 "GROUP BY theme_name,base_theme_name;";
//         System.out.println("before1:" + sql);
//         newSql = processor.addOrderBy(sql, "base_theme_name", true);
//         System.out.println("after1:" + newSql);
//         sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                 "SELECT x.theme_name,(" +
//                 "SELECT theme_name FROM (" +
//                 "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                 "GROUP BY theme_name,base_theme_name ORDER BY base_theme_name Desc;";
//         System.out.println("before2:" + sql);
//         newSql = processor.addOrderBy(sql, "base_theme_name", true);
//         System.out.println("after2:" + newSql);
//     }
//
//    /**
//     * 测试简单解析器的添加排序功能
//     * @author chenrui
//     * @date 2024/9/27 19:14
//     */
//    @Test
//    public void testAddOrderSimpleParser() {
//        SimpleSqlProcessor processor = new SimpleSqlProcessor();
//         // 复杂嵌套语句
//        String sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                "SELECT x.theme_name,(" +
//                "SELECT theme_name FROM (" +
//                "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                "GROUP BY theme_name,base_theme_name ORDER BY theme_name2 Desc,theme_name1,theme_name;";
//        System.out.println("before:" + sql);
//        String result1 = processor.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                "SELECT x.theme_name,(" +
//                "SELECT theme_name FROM (" +
//                "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                "GROUP BY theme_name,base_theme_name;";
//        result1 = processor.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after1:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM (" +
//                "SELECT x.theme_name,(" +
//                "SELECT theme_name FROM (" +
//                "SELECT e.theme_name FROM BASE_INFO e WHERE e.THEME_CODE='3' ORDER BY e.VERSION*1 DESC) WHERE ROWNUM=1) AS base_theme_name FROM BUSINESS_INDEX x WHERE 1=1) " +
//                "GROUP BY theme_name,base_theme_name ORDER BY base_theme_name Desc;";
//        System.out.println("before2:" + sql);
//        result1 = processor.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after2:" + result1);
//        // 普通语句
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM ABC order By theme_name DESC";
//        System.out.println("before3:" + sql);
//        result1 = processor.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after3:" + result1);
//        sql = "SELECT COUNT(*) AS count,theme_name,base_theme_name FROM ABC";
//        System.out.println("before4:" + sql);
//        result1 = processor.addOrderBy(sql, "base_theme_name", true);
//        System.out.println("after4:" + result1);
//    }
//
//    /**
//     * 测试miniDaoUtil:移除order by 当有 mybatis占位符时是否正常
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testRemoveOrderWithMybatis() {
//        String sql = "SELECT * FROM sys_user WHERE 1=1 AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
//        System.out.println("before:" + sql);
//        String result = MiniDaoUtil.removeOrderBy(sql);
//        System.out.println("after:" + result);
//        Assert.assertTrue(result.contains("#{params.username}"));
//    }
//
//    /**
//     * 测试miniDaoUtil:获取count语句 当有 mybatis占位符时是否正常
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testCountWithMybatis() {
//        String sql = "SELECT * FROM sys_user WHERE 1=1 AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
//        System.out.println("before:" + sql);
//        String result = MiniDaoUtil.getCountSql(sql);
//        System.out.println("after:" + result);
//        Assert.assertTrue(result.contains("#{params.username}"));
//    }
//
//    /**
//     * 测试miniDaoUtil:添加order by 当有 mybatis占位符时是否正常
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddOrderWithMybatis() {
//        String sql = "SELECT * FROM sys_user WHERE 1=1 AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
//        System.out.println("before:" + sql);
//        String result = MiniDaoUtil.addOrderBy(sql,"sex", false);
//        System.out.println("after:" + result);
//        Assert.assertTrue(result.contains("sex DESC"));
//    }
//
//    /**
//     * 测试复杂SQL的分页和count
//     * @author chenrui
//     * @date 2025/9/2 14:40
//     */
//    @Test
//    public void testWithComplexSql() {
//        String sql = "SELECT * FROM ( SELECT jrc.* FROM ( SELECT CONCAT('1',jrc.update_time) as ord, jrc.ID, jrc.NAME, jrc.SOURCE_TYPE as type, jrc.UPDATE_TIME FROM jimu_report_category jrc WHERE jrc.DEL_FLAG = 1 AND jrc.SOURCE_TYPE = 'report' ) jrc UNION ALL SELECT jr.* FROM ( SELECT CONCAT('0',jr.update_time) as ord, jr.ID, jr.NAME, jr.TYPE, jr.UPDATE_TIME FROM jimu_report jr WHERE jr.DEL_FLAG = 1 AND jr.TEMPLATE = 0 ) jr ) jm order by jm.ord desc,jm.update_time desc";
//        System.out.println("[count]before:" + sql);
//        String countSql = new JsqlparserSqlProcessor49().getCountSql(sql);
//        System.out.println("[count]after::" + countSql);
//        Assert.assertTrue(countSql.contains("count(0) table_count"));
//
//        System.out.println("[page]before:" + sql);
//        MiniDaoPage miniDaoPage = new MiniDaoPage();
//        miniDaoPage.setPage(1);
//        miniDaoPage.setRows(10);
//        String pageSql = new JsqlparserSqlProcessor49().getSqlServerPageSql(sql, miniDaoPage);
//        System.out.println("[page]after::" + pageSql);
//        Assert.assertTrue(pageSql.contains("ROW_NUMBER() OVER (ORDER BY ord DESC, update_time DESC) PAGE_ROW_NUMBER"));
//
//    }
//
//}
