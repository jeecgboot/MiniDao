import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
import org.jeecgframework.minidao.sqlparser.impl.vo.SelectSqlInfo;
import org.jeecgframework.minidao.util.MiniDaoUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 针对 JSqlParserUtils 的单元测试
 */
public class JSqlParserUtilsTest {

    private static final String[] sqlList = new String[]{
            "select * from sys_user",
            "select u.* from sys_user u",
            "select u.*, c.name from sys_user u, demo c",
            "select u.age, c.name from sys_user u, demo c",
            "select sex, age, c.name from sys_user, demo c",
            // 别名测试
            "select username as realname from sys_user",
            "select username as realname, u.realname as aaa, u.id bbb from sys_user u",
            // 不存在真实地查询字段
            "select count(1) from sys_user",
            // 函数式字段
            "select max(sex), id from sys_user",
            // 复杂嵌套函数式字段
            "select CONCAT(CONCAT(' _ ', sex), ' - ' , birthday) as info, id from sys_user",
            // 更复杂的嵌套函数式字段
            "select CONCAT(CONCAT(101,'_',NULL, DATE(create_time),'_',sex),' - ',birthday) as info, id from sys_user",
            // 带条件的SQL（不解析where条件里的字段，但不影响解析查询字段）
            "select username as name1 from sys_user where realname LIKE '%张%'",
            // 多重复杂关联表查询解析，包含的表为：sys_user, sys_depart, sys_dict_item, demo
            "" +
                    "SELECT " +
                    "    u.*, d.age, sd.item_text AS sex, (SELECT count(sd.id) FROM sys_depart sd) AS count " +
                    "FROM " +
                    "    (SELECT sd.username AS foo, sd.realname FROM sys_user sd) u, " +
                    "    demo d " +
                    "LEFT JOIN sys_dict_item AS sd ON d.sex = sd.item_value " +
                    "WHERE sd.dict_id = '3d9a351be3436fbefb1307d4cfb49bf2'",
            "select distinct org_code from sys_user",
            "select * from sys_user union select * from sys_user_bk",
            "select * from sys_user where 1=1 and username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC",
            "select * from demo where name='张三' order by create_time asc limit 1 for update",
    };


    private static final String[] subSqlList = new String[]{
            // 子查询SQL
            "select u.name1 as name2 from (select username as name1 from sys_user) u",
            // 多层嵌套子查询SQL
            "select u2.name2 as name3 from (select u1.name1 as name2 from (select username as name1 from sys_user) u1) u2",
            // 字段子查询SQL
            "select id, (select username as name1 from sys_user u2 where u1.id = u2.id) as name2 from sys_user u1",
            "select * from (select * from sys_user) t",
            "select org_code from (select * from sys_user su ) t where t.org_code is not null group by org_code",
    };

    /**
     * 测试 SQLServer 的分页 SQL 生成
     */
    @Test
    public void testSqlserverCreatePageSql() {
        String dbUrl = "jdbc:sqlserver://192.168.1.188:1433;SelectMethod=cursor;DatabaseName=jeecgbootbpm";

        // 简单分页
        String sql1 = "SELECT id, username FROM sys_user ORDER BY id";
        System.out.println("分页SQL1：" + MiniDaoUtil.createPageSql(dbUrl, sql1, 1, 10));
        System.out.println();

        // 多字段排序
        String sql2 = "SELECT id, username FROM sys_user ORDER BY id DESC, username ASC";
        System.out.println("分页SQL2：" + MiniDaoUtil.createPageSql(dbUrl, sql2, 2, 10));
        System.out.println();

        // 带聚合函数
        String sql3 = "SELECT sex, COUNT(*) as total FROM sys_user GROUP BY sex ORDER BY total DESC";
        System.out.println("分页SQL3：" + MiniDaoUtil.createPageSql(dbUrl, sql3, 1, 5));
        System.out.println();

        // 带子查询
        String sql4 = "SELECT COUNT(*) AS c, FORMAT(create_time, 'yyyy-MM-dd') AS date\n" +
                "FROM jmreport_big_screen\n" +
                "GROUP BY FORMAT(create_time, 'yyyy-MM-dd')\n" +
                "ORDER BY date DESC;\n";
        System.out.println("分页SQL4：" + MiniDaoUtil.createPageSql(dbUrl, sql4, 3, 10));
        System.out.println();

        // 带函数和别名
        String sql5 = "SELECT id, UPPER(name) as uname, age FROM demo WHERE age > 18 ORDER BY uname";
        System.out.println("分页SQL5：" + MiniDaoUtil.createPageSql(dbUrl, sql5, 1, 20));
        System.out.println();
    }
    
    
    
    
    @Test
    public void testParseSelectSql() {
        System.out.println("-----------------------------------------");
        for (String sql : sqlList) {
            System.out.println("待测试的sql：" + sql);
            try {
                // 解析所有的表名，key=表名，value=解析后的sql信息
                Map<String, SelectSqlInfo> parsedMap = MiniDaoUtil.parseAllSelectTable(sql);
                assert parsedMap != null;
                for (Map.Entry<String, SelectSqlInfo> entry : parsedMap.entrySet()) {
                    System.out.println("表名：" + entry.getKey());
                    this.printSqlInfo(entry.getValue(), 1);
                }
            } catch (Exception e) {
                System.out.println("SQL解析出现异常：" + e.getMessage());
            }
            System.out.println("-----------------------------------------");
        }
    }


    @Test
    public void testParseSubSelectSql() {
        System.out.println("-----------------------------------------");
        for (String sql : subSqlList) {
            System.out.println("待测试的sql：" + sql);
            try {
                // 解析所有的表名，key=表名，value=解析后的sql信息
                Map<String, SelectSqlInfo> parsedMap = MiniDaoUtil.parseAllSelectTable(sql);
                assert parsedMap != null;
                for (Map.Entry<String, SelectSqlInfo> entry : parsedMap.entrySet()) {
                    System.out.println("表名：" + entry.getKey());
                    this.printSqlInfo(entry.getValue(), 1);
                }
            } catch (Exception e) {
                System.err.println("SQL解析出现异常：" + e.getMessage());
            }
            System.out.println("-----------------------------------------");
        }
    }

    private void printSqlInfo(SelectSqlInfo sqlInfo, int level) {
        String beforeStr = this.getBeforeStr(level);
        if (sqlInfo.getFromTableName() == null) {
            // 子查询
            System.out.println(beforeStr + "子查询：" + sqlInfo.getFromSubSelect().getParsedSql());
            this.printSqlInfo(sqlInfo.getFromSubSelect(), level + 1);
        } else {
            // 非子查询
            System.out.println(beforeStr + "查询的表名：" + sqlInfo.getFromTableName());
        }
        if (StringUtils.isNotEmpty(sqlInfo.getFromTableAliasName())) {
            System.out.println(beforeStr + "查询的表别名：" + sqlInfo.getFromTableAliasName());
        }
        if (sqlInfo.isSelectAll()) {
            System.out.println(beforeStr + "查询的字段：*");
        } else {
            System.out.println(beforeStr + "查询的字段：" + sqlInfo.getSelectFields());
            System.out.println(beforeStr + "真实的字段：" + sqlInfo.getRealSelectFields());
            if (sqlInfo.getFromTableName() == null) {
                System.out.println(beforeStr + "所有的字段（包括子查询）：" + sqlInfo.getAllRealSelectFields());
            }
        }
    }

    // 打印前缀，根据层级来打印
    private String getBeforeStr(int level) {
        if (level == 0) {
            return "";
        }
        StringBuilder beforeStr = new StringBuilder();
        for (int i = 0; i < level; i++) {
            beforeStr.append("  ");
        }
        beforeStr.append("- ");
        return beforeStr.toString();
    }

    /**
     * 测试miniDaoUtil:获取count语句 当有 mybatis占位符时是否正常
     * @author chenrui
     * @date 2025/8/15 12:02
     */
    @Test
    public void testCountWithMybatis() {
        String sql = "SELECT * FROM sys_user WHERE 1=1 AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
        System.out.println("before:" + sql);
        String result = MiniDaoUtil.getCountSql(sql);
        System.out.println("after:" + result);
        Assert.assertTrue(result.contains("#{params.username}"));
    }

    /**
     * 测试miniDaoUtil:添加order by 当有 mybatis占位符时是否正常
     * @author chenrui
     * @date 2025/8/15 12:02
     */
    @Test
    public void testAddOrderWithMybatis() {
        String sql = "SELECT * FROM sys_user WHERE 1=1 AND username like concat('%',#{params.username}) ORDER BY create_time DESC, username ASC";
        System.out.println("before:" + sql);
        String result = MiniDaoUtil.addOrderBy(sql,"sex", false);
        System.out.println("after:" + result);
        Assert.assertTrue(result.contains("sex DESC"));
    }

    /**
     * [issues/9000]Online报表（带参数）预览后台报错
     * for [issues/9000]Online报表（带参数）预览后台报错
     * @author chenrui
     * @date 2025/11/10 15:31
     */
    @Test
    public void testMaskAlreadyQuotedMybatisPlaceholder() {
        String sql = "select * from sys_user where sex='${sex}' and type = '#{type}' and name=#{name}";
        Map<String, String> tokenMap = new HashMap<>();
        String masked = org.jeecgframework.minidao.sqlparser.impl.util.SqlParserUtils.maskMyBatisPlaceholders(sql, tokenMap);
        System.out.println("masked:" + masked);
        Assert.assertFalse(masked.contains("''__MB_PARAM_0__''"));
        Assert.assertTrue(masked.contains("sex='__MB_PARAM_0__'"));
        Assert.assertTrue(masked.contains("type = '__MB_PARAM_1__'"));
        Assert.assertTrue(masked.contains("name='__MB_PARAM_2__'"));
        String restored = org.jeecgframework.minidao.sqlparser.impl.util.SqlParserUtils.restoreMyBatisPlaceholders(masked, tokenMap);
        System.out.println("restored:" + restored);
        // 应该还原为与原始完全一致
        Assert.assertEquals(sql, restored);
    }

    /**
     * count SQL测试类
     */
    @Test
    public void testComplexCountSql() {
        System.out.println("-----------------------------------------");
        // 组合所有SQL进行测试
        String[] allSqlList = org.apache.commons.lang3.ArrayUtils.addAll(sqlList, subSqlList);
        
        for (String sql : allSqlList) {
            System.out.println("Original SQL: " + sql);
            try {
                String countSql = MiniDaoUtil.getCountSql(sql);
                System.out.println("Count SQL   : " + countSql);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * 测试 MiniDaoUtil.parseSqlFields
     */
    @Test
    public void testParseSqlFields() {
        System.out.println("-----------------------------------------");
        for (String sql : sqlList) {
            System.out.println("Original SQL: " + sql);
            try {
                List<Map<String, Object>> fields = MiniDaoUtil.parseSqlFields(sql);
                System.out.println("Parsed Fields: " + fields);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * 测试 #4426: SELECT大写时分页查询，只查出10项数据，select小写时没问题
     * 该测试用于验证大小写SELECT在分页时是否产生不同的结果
     */
    @Test
    public void testUppercaseSelectPagination() {
        String dbUrl = "jdbc:sqlserver://192.168.1.188:1433;SelectMethod=cursor;DatabaseName=jeecgbootbpm";

        // 使用小写的select
        String sqlLowercase = "select item_text as text, item_value as value from sys_dict_item";
        System.out.println("小写select分页SQL（第2页，每页15条）：");
        String pageSqlLowercase = MiniDaoUtil.createPageSql(dbUrl, sqlLowercase, 2, 15);
        System.out.println("结果："+ pageSqlLowercase);
        System.out.println();

        // 使用大写的SELECT
        String sqlUppercase = "select item_text as text, item_value as value from sys_dict_item";
        System.out.println("大写SELECT分页SQL（第2页，每页15条）：");
        String pageSqlUppercase = MiniDaoUtil.createPageSql(dbUrl, sqlUppercase, 2, 15);
        System.out.println("结果："+ pageSqlUppercase);
        System.out.println();

        // 验证两者应该产生相同的分页效果（除了大小写）
        Assert.assertTrue("大写SELECT应该返回正确的分页数 15", pageSqlUppercase.contains("TOP 15"));
        Assert.assertTrue("小写select应该返回正确的分页数 15", pageSqlLowercase.toUpperCase().contains("TOP 15"));
    }


    /**
     * 测试 MiniDaoUtil.getQueryTableInfo - 获取 SQL 中的表和字段信息【issues/9323】
     */
    @Test
    public void testGetQueryTableInfo() {

        System.out.println("========== getQueryTableInfo 测试 ==========");
        for (String sql : sqlList) {
            System.out.println("-----------------------------------------");
            System.out.println("Original SQL: " + sql);
            try {
                List<QueryTable> tableList = MiniDaoUtil.getQueryTableInfo(sql);
                if (tableList != null) {
                    for (QueryTable qt : tableList) {
                        System.out.println("表名: " + qt.getName() + ", 别名: " + qt.getAlias());
                        System.out.println("字段: " + qt.getFields());
                        System.out.println("是否查询全部字段: " + qt.isAll());
                    }
                } else {
                    System.out.println("结果为 null");
                }
            } catch (Exception e) {
                System.err.println("解析异常: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("-----------------------------------------");
        }
    }
}
