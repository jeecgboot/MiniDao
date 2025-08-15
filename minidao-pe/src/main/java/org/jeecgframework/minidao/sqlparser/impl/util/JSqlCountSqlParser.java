//package org.jeecgframework.minidao.sqlparser.impl.util;
//
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.Alias;
//import net.sf.jsqlparser.expression.Expression;
//import net.sf.jsqlparser.expression.Function;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.*;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * sql解析类，提供更智能的count查询sql
// *
// * @author jeecg
// */
//public class JSqlCountSqlParser {
//    private static final Log logger = LogFactory.getLog(JSqlCountSqlParser.class);
//    public static final String KEEP_ORDERBY = "/*keep orderby*/";
//    private static final Alias TABLE_ALIAS;
//    /**
//     * 匹配:user.name这样的参数表达式
//     */
//    public static Pattern dynamic = Pattern.compile(":[ tnx0Bfr]*[0-9a-z.A-Z_]+");
//    public static String DIAN = ".";
//    public static String DIAN_TMP = "@@@";
//
//    //<editor-fold desc="聚合函数">
//    private final Set<String> skipFunctions = Collections.synchronizedSet(new HashSet<String>());
//    private final Set<String> falseFunctions = Collections.synchronizedSet(new HashSet<String>());
//
//    /**
//     * 聚合函数，以下列函数开头的都认为是聚合函数
//     */
//    private static final Set<String> AGGREGATE_FUNCTIONS = new HashSet<String>(Arrays.asList(
//            ("APPROX_COUNT_DISTINCT," +
//                    "ARRAY_AGG," +
//                    "AVG," +
//                    "BIT_," +
//                    //"BIT_AND," +
//                    //"BIT_OR," +
//                    //"BIT_XOR," +
//                    "BOOL_," +
//                    //"BOOL_AND," +
//                    //"BOOL_OR," +
//                    "CHECKSUM_AGG," +
//                    "COLLECT," +
//                    "CORR," +
//                    //"CORR_," +
//                    //"CORRELATION," +
//                    "COUNT," +
//                    //"COUNT_BIG," +
//                    "COVAR," +
//                    //"COVAR_POP," +
//                    //"COVAR_SAMP," +
//                    //"COVARIANCE," +
//                    //"COVARIANCE_SAMP," +
//                    "CUME_DIST," +
//                    "DENSE_RANK," +
//                    "EVERY," +
//                    "FIRST," +
//                    "GROUP," +
//                    //"GROUP_CONCAT," +
//                    //"GROUP_ID," +
//                    //"GROUPING," +
//                    //"GROUPING," +
//                    //"GROUPING_ID," +
//                    "JSON_," +
//                    //"JSON_AGG," +
//                    //"JSON_ARRAYAGG," +
//                    //"JSON_OBJECT_AGG," +
//                    //"JSON_OBJECTAGG," +
//                    //"JSONB_AGG," +
//                    //"JSONB_OBJECT_AGG," +
//                    "LAST," +
//                    "LISTAGG," +
//                    "MAX," +
//                    "MEDIAN," +
//                    "MIN," +
//                    "PERCENT_," +
//                    //"PERCENT_RANK," +
//                    //"PERCENTILE_CONT," +
//                    //"PERCENTILE_DISC," +
//                    "RANK," +
//                    "REGR_," +
//                    "SELECTIVITY," +
//                    "STATS_," +
//                    //"STATS_BINOMIAL_TEST," +
//                    //"STATS_CROSSTAB," +
//                    //"STATS_F_TEST," +
//                    //"STATS_KS_TEST," +
//                    //"STATS_MODE," +
//                    //"STATS_MW_TEST," +
//                    //"STATS_ONE_WAY_ANOVA," +
//                    //"STATS_T_TEST_*," +
//                    //"STATS_WSR_TEST," +
//                    "STD," +
//                    //"STDDEV," +
//                    //"STDDEV_POP," +
//                    //"STDDEV_SAMP," +
//                    //"STDDEV_SAMP," +
//                    //"STDEV," +
//                    //"STDEVP," +
//                    "STRING_AGG," +
//                    "SUM," +
//                    "SYS_OP_ZONE_ID," +
//                    "SYS_XMLAGG," +
//                    "VAR," +
//                    //"VAR_POP," +
//                    //"VAR_SAMP," +
//                    //"VARIANCE," +
//                    //"VARIANCE_SAMP," +
//                    //"VARP," +
//                    "XMLAGG").split(",")));
//    //</editor-fold>
//
//    static {
//        TABLE_ALIAS = new Alias("table_count");
//        TABLE_ALIAS.setUseAs(false);
//    }
//
//    /**
//     * 添加到聚合函数，可以是逗号隔开的多个函数前缀
//     *
//     * @param functions
//     */
//    public static void addAggregateFunctions(String functions) {
//        if (StringUtils.isNotEmpty(functions)) {
//            String[] funs = functions.split(",");
//            for (int i = 0; i < funs.length; i++) {
//                AGGREGATE_FUNCTIONS.add(funs[i].toUpperCase());
//            }
//        }
//    }
//
//    /**
//     * 获取智能的countSql
//     *
//     * @param sql
//     * @return
//     */
//    public String getSmartCountSql(String sql) {
//        return getSmartCountSql(sql, "0");
//    }
//
//    /**
//     * 获取智能的countSql
//     *
//     * 处理body-去order by
//     * 
//     * @param sql
//     * @param countColumn 列名，默认 0
//     * @return
//     */
//    public String getSmartCountSql(String sql, String countColumn) {
//        //解析SQL
//        Statement stmt = null;
//        List<String> sqList = null;
//        String sqlOriginal = sql;
//        //特殊sql不需要去掉order by时，使用注释前缀
//        if (sql.indexOf(KEEP_ORDERBY) >= 0) {
//            return getSimpleCountSql(sql, countColumn);
//        }
//        //---------------------------------------------------------------------------------------------
//        // 如果包含mybatis变量，先将其替换为占位符，避免解析时出错
//        Map<String, String> mbMap = new LinkedHashMap<>();
//        sql = SqlParserUtils.maskMyBatisPlaceholders(sql, mbMap);
//        //---------------------------------------------------------------------------------------------
//        try {
//            stmt = CCJSqlParserUtil.parse(sql);
//        } catch (Throwable e) {
//            //--------带点处理---------------------------------------------------------------------------------------
//            try {
//                //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
//                if (e.toString().indexOf(DIAN) != -1) {
//                    sqList = getKeyListByContent(sql);
//                    for (String s : sqList) {
//                        sql = sql.replace(s, s.replace(DIAN, DIAN_TMP));
//                    }
//                    logger.debug(" --- JSQLParser with DIAN --- convert begin sql = " + sql);
//                    stmt = CCJSqlParserUtil.parse(sql);
//                } else {
//                    //无法解析的用一般方法返回count语句
//                    return getSimpleCountSql(sqlOriginal, countColumn);
//                }
//            } catch (JSQLParserException e1) {
//                logger.warn("无法解析用普通方式返回count语句: " + (e1.getMessage() != null ? e1.getMessage() : e1.getCause().getMessage()));
//                //e1.printStackTrace();
//                //无法解析的用一般方法返回count语句
//                return getSimpleCountSql(sqlOriginal, countColumn);
//            }
//            //----------带点处理-------------------------------------------------------------------------------------
//        }
//        Select select = (Select) stmt;
//        SelectBody selectBody = select.getSelectBody();
//        try {
//            //处理body-去order by
//            processSelectBody(selectBody);
//        } catch (Exception e) {
//            //当 sql 包含 group by 时，不去除 order by
//            return getSimpleCountSql(sqlOriginal, countColumn);
//        }
//        //处理with-去order by
//        processWithItemsList(select.getWithItemsList());
//        //处理为count查询
//        sqlToCount(select, countColumn);
//        String result = select.toString();
//
//        //------带点处理-----------------------------------------------------------------------------------------
//        //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
//        if (sqList != null) {
//            for (String s : sqList) {
//                result = result.replace(s.replace(DIAN, DIAN_TMP), s.replace(DIAN_TMP, DIAN));
//            }
//            logger.debug(" --- JSQLParser with DIAN --- convert end sql = " + result);
//        }
//        //-----带点处理-------------------------------------------------------------------------------------------
//        //---------------------------------------------------------------------------------------------
//        // 如果包含mybatis变量，恢复占位符
//        result = SqlParserUtils.restoreMyBatisPlaceholders(result, mbMap);
//        //---------------------------------------------------------------------------------------------
//        return result;
//    }
//
//    /**
//     * 按照动态内容的参数出现顺序,将参数放到List中
//     *
//     * @param content
//     * @return
//     */
//    public static List<String> getKeyListByContent(String content) {
//        Set<String> paramSet = new LinkedHashSet<>();
//        Matcher m = dynamic.matcher(content);
//        while (m.find()) {
//            if (m.group() != null && m.group().indexOf(DIAN) != -1) {
//                paramSet.add(m.group());
//            }
//        }
//        return new ArrayList<>(paramSet);
//    }
//
//    /**
//     * 获取普通的Count-sql
//     *
//     * @param sql 原查询sql
//     * @return 返回count查询sql
//     */
//    public String getSimpleCountSql(final String sql) {
//        return getSimpleCountSql(sql, "0");
//    }
//
//    /**
//     * 获取普通的Count-sql
//     *
//     * @param sql 原查询sql
//     * @return 返回count查询sql
//     */
//    public String getSimpleCountSql(final String sql, String name) {
//        StringBuilder stringBuilder = new StringBuilder(sql.length() + 40);
//        stringBuilder.append("select count(");
//        stringBuilder.append(name);
//        stringBuilder.append(") from ( \n");
//        stringBuilder.append(sql);
//        stringBuilder.append("\n ) tmp_count");
//        return stringBuilder.toString();
//    }
//
//    /**
//     * 将sql转换为count查询
//     *
//     * @param select
//     */
//    public void sqlToCount(Select select, String name) {
//        SelectBody selectBody = select.getSelectBody();
//        // 是否能简化count查询
//        List<SelectItem> COUNT_ITEM = new ArrayList<SelectItem>();
//        COUNT_ITEM.add(new SelectExpressionItem(new Column("count(" + name + ")")));
//        if (selectBody instanceof PlainSelect && isSimpleCount((PlainSelect) selectBody)) {
//            ((PlainSelect) selectBody).setSelectItems(COUNT_ITEM);
//        } else {
//            PlainSelect plainSelect = new PlainSelect();
//            SubSelect subSelect = new SubSelect();
//            subSelect.setSelectBody(selectBody);
//            subSelect.setAlias(TABLE_ALIAS);
//            plainSelect.setFromItem(subSelect);
//            plainSelect.setSelectItems(COUNT_ITEM);
//            select.setSelectBody(plainSelect);
//        }
//    }
//
//    /**
//     * 是否可以用简单的count查询方式
//     *
//     * @param select
//     * @return
//     */
//    public boolean isSimpleCount(PlainSelect select) {
//        //包含group by的时候不可以
//        if (select.getGroupBy() != null) {
//            return false;
//        }
//        //包含distinct的时候不可以
//        if (select.getDistinct() != null) {
//            return false;
//        }
//        for (SelectItem item : select.getSelectItems()) {
//            //select列中包含参数的时候不可以，否则会引起参数个数错误
//            if (item.toString().contains("?")) {
//                return false;
//            }
//            //如果查询列中包含函数，也不可以，函数可能会聚合列
//            if (item instanceof SelectExpressionItem) {
//                Expression expression = ((SelectExpressionItem) item).getExpression();
//                if (expression instanceof Function) {
//                    String name = ((Function) expression).getName();
//                    if (name != null) {
//                        String NAME = name.toUpperCase();
//                        if (skipFunctions.contains(NAME)) {
//                            //go on
//                        } else if (falseFunctions.contains(NAME)) {
//                            return false;
//                        } else {
//                            for (String aggregateFunction : AGGREGATE_FUNCTIONS) {
//                                if (NAME.startsWith(aggregateFunction)) {
//                                    falseFunctions.add(NAME);
//                                    return false;
//                                }
//                            }
//                            skipFunctions.add(NAME);
//                        }
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 处理selectBody去除Order by
//     *
//     * @param selectBody
//     */
//    public void processSelectBody(SelectBody selectBody) {
//        if (selectBody instanceof PlainSelect) {
//            processPlainSelect((PlainSelect) selectBody);
//        } else if (selectBody instanceof WithItem) {
//            WithItem withItem = (WithItem) selectBody;
//            if (JSqlSubSelectBody.getItemSelectBody(withItem)!=null) {
//                processSelectBody(JSqlSubSelectBody.getItemSelectBody(withItem));
//            }
//        } else {
//            SetOperationList operationList = (SetOperationList) selectBody;
//            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
//                List<SelectBody> plainSelects = operationList.getSelects();
//                for (SelectBody plainSelect : plainSelects) {
//                    processSelectBody(plainSelect);
//                }
//            }
//            if (!orderByHashParameters(operationList.getOrderByElements())) {
//                operationList.setOrderByElements(null);
//            }
//        }
//    }
//
//    /**
//     * 处理PlainSelect类型的selectBody
//     *
//     * @param plainSelect
//     */
//    public void processPlainSelect(PlainSelect plainSelect) {
//        if (!orderByHashParameters(plainSelect.getOrderByElements())) {
//            plainSelect.setOrderByElements(null);
//        }
//        if (plainSelect.getFromItem() != null) {
//            processFromItem(plainSelect.getFromItem());
//        }
//        if (plainSelect.getJoins() != null && plainSelect.getJoins().size() > 0) {
//            List<Join> joins = plainSelect.getJoins();
//            for (Join join : joins) {
//                if (join.getRightItem() != null) {
//                    processFromItem(join.getRightItem());
//                }
//            }
//        }
//    }
//
//    /**
//     * 处理WithItem
//     *
//     * @param withItemsList
//     */
//    public void processWithItemsList(List<WithItem> withItemsList) {
//        if (withItemsList != null && withItemsList.size() > 0) {
//            for (WithItem item : withItemsList) {
//                processSelectBody(JSqlSubSelectBody.getItemSelectBody(item));
//            }
//        }
//    }
//
//    /**
//     * 处理子查询
//     *
//     * @param fromItem
//     */
//    public void processFromItem(FromItem fromItem) {
//        if (fromItem instanceof SubJoin) {
//            SubJoin subJoin = (SubJoin) fromItem;
//            if (subJoin.getJoinList() != null && subJoin.getJoinList().size() > 0) {
//                for (Join join : subJoin.getJoinList()) {
//                    if (join.getRightItem() != null) {
//                        processFromItem(join.getRightItem());
//                    }
//                }
//            }
//            if (subJoin.getLeft() != null) {
//                processFromItem(subJoin.getLeft());
//            }
//        } else if (fromItem instanceof SubSelect) {
//            SubSelect subSelect = (SubSelect) fromItem;
//            if (subSelect.getSelectBody() != null) {
//                processSelectBody(subSelect.getSelectBody());
//            }
//        } else if (fromItem instanceof ValuesList) {
//
//        } else if (fromItem instanceof LateralSubSelect) {
//            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
//            if (lateralSubSelect.getSubSelect() != null) {
//                SubSelect subSelect = lateralSubSelect.getSubSelect();
//                if (subSelect.getSelectBody() != null) {
//                    processSelectBody(subSelect.getSelectBody());
//                }
//            }
//        }
//        //Table时不用处理
//    }
//
//    /**
//     * 判断Orderby是否包含参数，有参数的不能去
//     *
//     * @param orderByElements
//     * @return
//     */
//    public boolean orderByHashParameters(List<OrderByElement> orderByElements) {
//        if (orderByElements == null) {
//            return false;
//        }
//        for (OrderByElement orderByElement : orderByElements) {
//            if (orderByElement.toString().contains("?")) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
