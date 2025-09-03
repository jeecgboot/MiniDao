//package test;
//
//import org.jeecgframework.minidao.sqlparser.AbstractSqlProcessor;
//import org.jeecgframework.minidao.sqlparser.impl.JsqlparserSqlProcessor49;
//import org.jeecgframework.minidao.sqlparser.impl.SimpleSqlProcessor;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * 测试SQL处理器中addWhereCondition方法
// * 分别测试SimpleSqlProcessor和JsqlparserSqlProcessor
// *
// * @author chenrui
// * @date 2025/8/15 12:02
// */
//public class SqlParserWhereTest {
//    private AbstractSqlProcessor simpleSqlProcessor;
//    private AbstractSqlProcessor jsqlparserSqlProcessor;
//
//    @Before
//    public void setUp() {
//        simpleSqlProcessor = new SimpleSqlProcessor();
//        jsqlparserSqlProcessor = new JsqlparserSqlProcessor49();
//    }
//
//    /**
//     * 测试简单SQL添加条件（无已有WHERE子句）
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionSimple() {
//        String sql = "SELECT * FROM user";
//        String condition = "username = 'admin'";
//        String expected = "SELECT * FROM user WHERE username = 'admin'";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertEquals(expected, simpleResult);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertEquals(expected, jsqlResult);
//    }
//
//    /**
//     * 测试添加条件到已有WHERE子句的SQL
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithExistingWhere() {
//        String sql = "SELECT * FROM user WHERE status = 'active'";
//        String condition = "username = 'admin'";
//        String expected = "SELECT * FROM user WHERE status = 'active' AND username = 'admin'";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertEquals(expected, simpleResult);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertEquals(expected, jsqlResult);
//    }
//
//    /**
//     * 测试添加条件到带有ORDER BY子句的SQL
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithOrderBy() {
//        String sql = "SELECT * FROM user ORDER BY username ASC";
//        String condition = "status = 'active'";
//        String expected = "SELECT * FROM user WHERE status = 'active' ORDER BY username ASC";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertEquals(expected, simpleResult);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertEquals(expected, jsqlResult);
//    }
//
//    /**
//     * 测试使用字段、值和操作符添加条件
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithFieldValueOperator() {
//        String sql = "SELECT * FROM product";
//        String expected = "SELECT * FROM product WHERE price = 100";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, "price", 100, "=");
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertEquals(expected, simpleResult);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, "price", 100, "=");
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertEquals(expected, jsqlResult);
//    }
//
//    /**
//     * 测试使用不同操作符添加条件
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithDifferentOperators() {
//        String sql = "SELECT * FROM product";
//
//        // 测试大于操作符
//        String expectedGt = "SELECT * FROM product WHERE price > 100";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 (>) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expectedGt);
//        String simpleResultGt = simpleSqlProcessor.addWhereCondition(sql, "price", 100, ">");
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResultGt);
//        Assert.assertEquals(expectedGt, simpleResultGt);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 (>) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expectedGt);
//        String jsqlResultGt = jsqlparserSqlProcessor.addWhereCondition(sql, "price", 100, ">");
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResultGt);
//        Assert.assertEquals(expectedGt, jsqlResultGt);
//
//        // 测试LIKE操作符
//        String expectedLike = "SELECT * FROM product WHERE name LIKE '%手机%'";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 (LIKE) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expectedLike);
//        String simpleResultLike = simpleSqlProcessor.addWhereCondition(sql, "name", "%手机%", "like");
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResultLike);
//        Assert.assertTrue(simpleResultLike.contains("name") && simpleResultLike.contains("手机"));
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 (LIKE) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expectedLike);
//        String jsqlResultLike = jsqlparserSqlProcessor.addWhereCondition(sql, "name", "%手机%", "like");
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResultLike);
//        Assert.assertTrue(jsqlResultLike.contains("name") && jsqlResultLike.contains("手机"));
//
//        // 测试IN操作符
//        System.out.println("============ SimpleSqlProcessor 测试 (IN) ============\n");
//        String simpleResultIn = simpleSqlProcessor.addWhereCondition(sql, "category", "电子,家电,数码", "in");
//        System.out.println("SimpleSqlProcessor实际SQL(IN): " + simpleResultIn);
//        Assert.assertTrue(simpleResultIn.contains("category") && simpleResultIn.contains("in"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (IN) ============\n");
//        String jsqlResultIn = jsqlparserSqlProcessor.addWhereCondition(sql, "category", "电子,家电,数码", "in");
//        System.out.println("JsqlparserSqlProcessor实际SQL(IN): " + jsqlResultIn);
//        Assert.assertTrue(jsqlResultIn.contains("category") && jsqlResultIn.contains("电子"));
//    }
//
//    /**
//     * 测试复杂SQL (UNION)
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithUnion() {
//        String sql = "SELECT * FROM active_users UNION SELECT * FROM inactive_users";
//        String condition = "username = 'admin'";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 (UNION) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: 包含条件 'WHERE username = 'admin''");
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE") && simpleResult.contains("username = 'admin'"));
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 (UNION) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: 包含条件 'WHERE username = 'admin''");
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("WHERE") && jsqlResult.contains("username = 'admin'"));
//    }
//
//    /**
//     * 测试复杂SQL (子查询)
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithSubquery() {
//        String sql = "SELECT * FROM (SELECT * FROM user) AS u";
//        String condition = "u.username = 'admin'";
//        String expected = "SELECT * FROM (SELECT * FROM user) AS u WHERE u.username = 'admin'";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 (子查询) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertEquals(expected, simpleResult);
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 (子查询) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("期望SQL: " + expected);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertEquals(expected, jsqlResult);
//    }
//
//    /**
//     * 测试多条件添加
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddMultipleWhereConditions() {
//        String sql = "SELECT * FROM product";
//
//        // 测试SimpleSqlProcessor
//        System.out.println("============ SimpleSqlProcessor 测试 (多条件) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, "price", 100, ">");
//        System.out.println("添加第一个条件后: " + simpleResult);
//        simpleResult = simpleSqlProcessor.addWhereCondition(simpleResult, "category", "电子产品", "=");
//        System.out.println("添加第二个条件后: " + simpleResult);
//        simpleResult = simpleSqlProcessor.addWhereCondition(simpleResult, "name", "%手机%", "like");
//        System.out.println("添加第三个条件后: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("price > 100") && simpleResult.contains("category = '电子产品'") && (simpleResult.contains("name like '%手机%'") || simpleResult.contains("name LIKE '%手机%'")));
//
//        // 测试JsqlparserSqlProcessor
//        System.out.println("============ JsqlparserSqlProcessor 测试 (多条件) ============\n");
//        sql = "SELECT * FROM product"; // 重置SQL
//        System.out.println("原始SQL: " + sql);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, "price", 100, ">");
//        System.out.println("添加第一个条件后: " + jsqlResult);
//        jsqlResult = jsqlparserSqlProcessor.addWhereCondition(jsqlResult, "category", "电子产品", "=");
//        System.out.println("添加第二个条件后: " + jsqlResult);
//        jsqlResult = jsqlparserSqlProcessor.addWhereCondition(jsqlResult, "name", "%手机%", "like");
//        System.out.println("添加第三个条件后: " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("price > 100") && jsqlResult.contains("category = '电子产品'") && (jsqlResult.contains("name LIKE '%手机%'")));
//    }
//
//    /**
//     * 测试空值和空字符串
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithNullAndEmpty() {
//        String sql = "SELECT * FROM user";
//
//        // 测试NULL值条件
//        System.out.println("============ SimpleSqlProcessor 测试 (NULL值) ============\n");
//        String simpleResultNull = simpleSqlProcessor.addWhereCondition(sql, "username", null, "=");
//        System.out.println("SimpleSqlProcessor实际SQL(NULL): " + simpleResultNull);
//        Assert.assertTrue(simpleResultNull.contains("username = NULL"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (NULL值) ============\n");
//        String jsqlResultNull = jsqlparserSqlProcessor.addWhereCondition(sql, "username", null, "=");
//        System.out.println("JsqlparserSqlProcessor实际SQL(NULL): " + jsqlResultNull);
//        Assert.assertTrue(jsqlResultNull.contains("username = NULL"));
//
//        // 测试空字符串条件
//        System.out.println("============ SimpleSqlProcessor 测试 (空字符串) ============\n");
//        String simpleResultEmpty = simpleSqlProcessor.addWhereCondition(sql, "username", "", "=");
//        System.out.println("SimpleSqlProcessor实际SQL(空字符串): " + simpleResultEmpty);
//        Assert.assertTrue(simpleResultEmpty.contains("username = ''"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (空字符串) ============\n");
//        String jsqlResultEmpty = jsqlparserSqlProcessor.addWhereCondition(sql, "username", "", "=");
//        System.out.println("JsqlparserSqlProcessor实际SQL(空字符串): " + jsqlResultEmpty);
//        Assert.assertTrue(jsqlResultEmpty.contains("username = ''"));
//    }
//
//    /**
//     * 测试包含JOIN的SQL
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithJoin() {
//        String sql = "SELECT u.*, d.name as dept_name FROM user u JOIN department d ON u.dept_id = d.id";
//        String condition = "u.status = 'active'";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (JOIN) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE u.status = 'active'"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (JOIN) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("WHERE u.status = 'active'"));
//    }
//
//    /**
//     * 测试复杂SELECT语句
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithComplexSelect() {
//        String sql = "SELECT u.username, d.name, COUNT(o.id) as order_count " + "FROM user u " + "LEFT JOIN department d ON u.dept_id = d.id " + "LEFT JOIN orders o ON o.user_id = u.id " + "GROUP BY u.username, d.name " + "HAVING COUNT(o.id) > 5 " + "ORDER BY order_count DESC";
//
//        String condition = "u.status = 'active'";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (复杂SELECT) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE u.status = 'active'"));
//        Assert.assertTrue(simpleResult.contains("GROUP BY u.username, d.name"));
//        Assert.assertTrue(simpleResult.contains("HAVING COUNT(o.id) > 5"));
//        Assert.assertTrue(simpleResult.contains("ORDER BY order_count DESC"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (复杂SELECT) ============\n");
//        System.out.println("原始SQL: " + sql);
//        System.out.println("条件: " + condition);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("WHERE u.status = 'active'"));
//        Assert.assertTrue(jsqlResult.contains("GROUP BY u.username, d.name"));
//        Assert.assertTrue(jsqlResult.contains("HAVING COUNT(o.id) > 5"));
//        Assert.assertTrue(jsqlResult.contains("ORDER BY order_count DESC"));
//    }
//
//    /**
//     * 测试特殊字符转义
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithSpecialCharacters() {
//        String sql = "SELECT * FROM user";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (特殊字符) ============\n");
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, "comment", "O'Reilly's book", "=");
//        System.out.println("SimpleSqlProcessor实际SQL(特殊字符): " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("O''Reilly''s book"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (特殊字符) ============\n");
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, "comment", "O'Reilly's book", "=");
//        System.out.println("JsqlparserSqlProcessor实际SQL(特殊字符): " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("O''Reilly''s book"));
//    }
//
//    /**
//     * 测试带括号的SQL查询
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithParenthesis() {
//        String sql = "SELECT * FROM (SELECT id, name FROM user WHERE age > 18) AS adult_users";
//        String condition = "adult_users.name LIKE '%张%'";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (带括号) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE adult_users.name"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (带括号) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//        Assert.assertTrue(jsqlResult.contains("WHERE adult_users.name"));
//    }
//
//    /**
//     * 测试带WITH子句的SQL
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testAddWhereConditionWithWith() {
//        String sql = "WITH user_count AS (SELECT dept_id, COUNT(*) as count FROM user GROUP BY dept_id) " + "SELECT d.name, uc.count FROM department d JOIN user_count uc ON d.id = uc.dept_id";
//        String condition = "uc.count > 10";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (WITH子句) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE uc.count > 10"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (WITH子句) ============\n");
//        System.out.println("原始SQL: " + sql);
//        try {
//            String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//            System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//            Assert.assertTrue(jsqlResult.contains("WHERE uc.count > 10"));
//        } catch (Exception e) {
//            System.out.println("JsqlparserSqlProcessor无法处理WITH子句: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 测试带WITH子句的SQL
//     *
//     * @author chenrui
//     * @date 2025/8/15 12:02
//     */
//    @Test
//    public void testMybatis() {
//        String sql = "WITH user_count AS (SELECT dept_id, COUNT(*) as count FROM user GROUP BY dept_id) " + "SELECT d.name, uc.count FROM department d JOIN user_count uc ON d.id = uc.dept_id WHERE d.status = ${params.status}";
//        String condition = "name like #{param.name, jdbcType=VARCHAR}";
//
//        System.out.println("============ SimpleSqlProcessor 测试 (Mybatis) ============\n");
//        System.out.println("原始SQL: " + sql);
//        String simpleResult = simpleSqlProcessor.addWhereCondition(sql, condition);
//        System.out.println("SimpleSqlProcessor实际SQL: " + simpleResult);
//        Assert.assertTrue(simpleResult.contains("WHERE d.status = ${params.status} AND name like #{param.name, jdbcType=VARCHAR}"));
//
//        System.out.println("============ JsqlparserSqlProcessor 测试 (Mybatis) ============\n");
//        System.out.println("原始SQL: " + sql);
//        try {
//            String jsqlResult = jsqlparserSqlProcessor.addWhereCondition(sql, condition);
//            System.out.println("JsqlparserSqlProcessor实际SQL: " + jsqlResult);
//            Assert.assertTrue(jsqlResult.contains("WHERE d.status = ${params.status} AND name LIKE #{param.name, jdbcType=VARCHAR}"));
//        } catch (Exception e) {
//            System.out.println("JsqlparserSqlProcessor无法处理WITH子句: " + e.getMessage());
//        }
//    }
//
//
//}
