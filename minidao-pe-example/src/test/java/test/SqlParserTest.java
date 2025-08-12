//package test;
//
//import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor49;
//import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
//import org.jeecgframework.minidao.util.MiniDaoUtil;
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
//
//}
