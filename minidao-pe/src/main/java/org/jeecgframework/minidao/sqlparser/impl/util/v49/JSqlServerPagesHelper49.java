package org.jeecgframework.minidao.sqlparser.impl.util.v49;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.pagehelper.PageException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将sqlserver查询语句转换为分页语句<br>
 * 注意事项：<br>
 * <ol>
 * <li>请先保证你的SQL可以执行</li>
 * <li>sql中最好直接包含order by，可以自动从sql提取</li>
 * <li>如果没有order by，可以通过入参提供，但是需要自己保证正确</li>
 * <li>如果sql有order by，可以通过orderby参数覆盖sql中的order by</li>
 * <li>order by的列名不能使用别名</li>
 * <li>表和列使用别名的时候不要使用单引号(')</li>
 * </ol>
 * 该类设计为一个独立的工具类，依赖jsqlparser,可以独立使用
 */
public class JSqlServerPagesHelper49 {
    private static final Log logger = LogFactory.getLog(JSqlServerPagesHelper49.class);
    //开始行号
    public static final String START_ROW = String.valueOf(Long.MIN_VALUE);
    //结束行号
    public static final String PAGE_SIZE = String.valueOf(Long.MAX_VALUE);
    //外层包装表
    protected static final String WRAP_TABLE = "WRAP_OUTER_TABLE";
    //表别名名字
    protected static final String PAGE_TABLE_NAME = "PAGE_TABLE_ALIAS";
    //protected
    public static final Alias PAGE_TABLE_ALIAS = new Alias(PAGE_TABLE_NAME);
    //行号
    protected static final String PAGE_ROW_NUMBER = "PAGE_ROW_NUMBER";
    //行号列
    protected static final Column PAGE_ROW_NUMBER_COLUMN = new Column(PAGE_ROW_NUMBER);
    //TOP 100 PERCENT
    protected static final Top TOP100_PERCENT;
    //别名前缀
    protected static final String PAGE_COLUMN_ALIAS_PREFIX = "ROW_ALIAS_";

    /**
     * 匹配:user.name这样的参数表达式
     */
    public static Pattern dynamic = Pattern.compile(":[ tnx0Bfr]*[0-9a-z.A-Z_]+");
    public static String DIAN = ".";
    public static String DIAN_TMP = "@@@";


    //静态方法处理
    static {
        TOP100_PERCENT = new Top();
        TOP100_PERCENT.setExpression(new LongValue(100));
        TOP100_PERCENT.setPercentage(true);
    }

    /**
     * 转换为分页语句
     *
     * @param sql
     * @return
     */
    public String convertToPageSql(String sql) {
        return convertToPageSql(sql, null, null);
    }


    /**
     * 转换为分页语句
     *
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    public String convertToPageSql(String sql, Integer offset, Integer limit) {
        //解析SQL
        Statement stmt;
        List<String> sqList = null;
        String sqlOriginal = sql;

        try {
            //update-begin---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
            stmt = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
            //update-end---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
        } catch (JSQLParserException e) {
            //--------带点处理---------------------------------------------------------------------------------------
            try {
                //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
                if (e.toString().indexOf(DIAN) != -1) {
                    sqList = getKeyListByContent(sql);
                    for (String s : sqList) {
                        sql = sql.replace(s, s.replace(DIAN, DIAN_TMP));
                    }
                    logger.debug(" --- JSQLParser with DIAN --- convert begin sql = " + sql);
                    stmt = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
                } else {
                    throw new PageException("不支持该SQL转换为分页查询!", e);
                }
            } catch (JSQLParserException e1) {
                throw new PageException("不支持该SQL转换为分页查询!", e);
            }
            //----------带点处理-------------------------------------------------------------------------------------
        }
        if (!(stmt instanceof Select)) {
            throw new PageException("分页语句必须是Select查询!");
        }
        //获取分页查询的select
        Select pageSelect = getPageSelect((Select) stmt);
        String pageSql = pageSelect.toString();
        //缓存移到外面了，所以不替换参数
        if (offset != null) {
            pageSql = pageSql.replace(START_ROW, String.valueOf(offset));
        }
        if (limit != null) {
            pageSql = pageSql.replace(PAGE_SIZE, String.valueOf(limit));
        }

        //------带点处理-----------------------------------------------------------------------------------------
        //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
        if (sqList != null) {
            for (String s : sqList) {
                pageSql = pageSql.replace(s.replace(DIAN, DIAN_TMP), s.replace(DIAN_TMP, DIAN));
            }
            logger.debug(" --- JSQLParser with DIAN --- convert end sql = " + pageSql);
        }
        //-----带点处理-------------------------------------------------------------------------------------------
        return pageSql;
    }

    /**
     * 按照动态内容的参数出现顺序,将参数放到List中
     *
     * @param content
     * @return
     */
    public static List<String> getKeyListByContent(String content) {
        Set<String> paramSet = new LinkedHashSet<>();
        Matcher m = dynamic.matcher(content);
        while (m.find()) {
            if (m.group() != null && m.group().indexOf(DIAN) != -1) {
                paramSet.add(m.group());
            }
        }
        return new ArrayList<>(paramSet);
    }

    /**
     * 获取一个外层包装的TOP查询
     *
     * @param select
     * @return
     */
    protected Select getPageSelect(Select select) {
        if (select instanceof SetOperationList) {
            select = wrapSetOperationList((SetOperationList) select);
        }
        //这里的selectBody一定是PlainSelect
        if (((PlainSelect) select).getTop() != null) {
            throw new PageException("被分页的语句已经包含了Top，不能再通过分页插件进行分页查询!");
        }
        //获取查询列
        List<SelectItem<?>> selectItems = getSelectItems((PlainSelect) select);
        //对一层的SQL增加ROW_NUMBER()
        List<SelectItem<?>> autoItems = new ArrayList<>();
        SelectItem<?> orderByColumn = addRowNumber((PlainSelect) select, autoItems);
        //加入自动生成列
        ((PlainSelect) select).addSelectItems(autoItems.toArray(new SelectItem[autoItems.size()]));
        //处理子语句中的order by
        processSelectBody(select, 0);

        //中层子查询
        PlainSelect innerSelectBody = new PlainSelect();
        //PAGE_ROW_NUMBER
        innerSelectBody.addSelectItems(orderByColumn);
        innerSelectBody.addSelectItems(selectItems.toArray(new SelectItem[selectItems.size()]));


        //将原始查询作为内层子查询
        ParenthesedSelect fromInnerSelect = new ParenthesedSelect().withAlias(PAGE_TABLE_ALIAS);
        fromInnerSelect.setSelect(select);
        innerSelectBody.setFromItem(fromInnerSelect);

        //新建一个select
        PlainSelect newSelectBody = new PlainSelect();
        //设置top
        Top top = new Top();
        top.setExpression(new LongValue(Long.MAX_VALUE));
        newSelectBody.setTop(top);
        //设置order by
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        OrderByElement orderByElement = new OrderByElement();
        orderByElement.setExpression(PAGE_ROW_NUMBER_COLUMN);
        orderByElements.add(orderByElement);
        newSelectBody.setOrderByElements(orderByElements);
        //设置where
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.setLeftExpression(PAGE_ROW_NUMBER_COLUMN);
        greaterThan.setRightExpression(new LongValue(Long.MIN_VALUE));
        newSelectBody.setWhere(greaterThan);
        //设置selectItems
        newSelectBody.setSelectItems(selectItems);
        //设置fromIterm
        ParenthesedSelect fromItem = new ParenthesedSelect();
        fromItem.setSelect(innerSelectBody); //中层子查询
        fromItem.setAlias(PAGE_TABLE_ALIAS);
        newSelectBody.setFromItem(fromItem);

        if (isNotEmptyList(select.getWithItemsList())) {
            newSelectBody.setWithItemsList(select.getWithItemsList());
        }
        return newSelectBody;
    }

    /**
     * 包装SetOperationList
     *
     * @param setOperationList
     * @return
     */
    protected Select wrapSetOperationList(SetOperationList setOperationList) {
        //获取最后一个plainSelect
        Select setSelectBody = setOperationList.getSelects().get(setOperationList.getSelects().size() - 1);
        if (!(setSelectBody instanceof PlainSelect)) {
            throw new PageException("目前无法处理该SQL，您可以将该SQL发送给abel533@gmail.com协助作者解决!");
        }
        PlainSelect plainSelect = (PlainSelect) setSelectBody;
        PlainSelect selectBody = new PlainSelect();
        List<SelectItem<?>> selectItems = getSelectItems(plainSelect);
        selectBody.setSelectItems(selectItems);

        //设置fromIterm
        FromItem fromItem = new ParenthesedSelect().withAlias(new Alias(WRAP_TABLE)).withSelect(setOperationList);
        selectBody.setFromItem(fromItem);
        //order by
        if (isNotEmptyList(plainSelect.getOrderByElements())) {
            selectBody.setOrderByElements(plainSelect.getOrderByElements());
            plainSelect.setOrderByElements(null);
        }
        return selectBody;
    }

    /**
     * 获取查询列
     *
     * @param plainSelect
     * @return
     */
    protected List<SelectItem<?>> getSelectItems(PlainSelect plainSelect) {
        //设置selectItems
        List<SelectItem<?>> selectItems = new ArrayList<>();
        for (SelectItem<?> selectItem : plainSelect.getSelectItems()) {
            //update-begin---author:wangshuai---date:2025-06-05---for:【issues/3802】sprintboot3.3.6集成报表1.9.5，数据库是SQLserver。打开http://localhost:8080/jmreport/list报错，不能显示已有的报表---
            if(selectItem.getAlias() != null){
                //直接使用别名
                Column column = new Column(selectItem.getAlias().getName());
                selectItems.add(new SelectItem<>(column));
            }else {
                Expression expression = selectItem.getExpression();
                if (expression instanceof AllTableColumns) {
                    selectItems.add(new SelectItem<>(new AllColumns()));
                } else if (expression instanceof Column) {
                    Column column = (Column) expression;
                    if (column.getTable() != null) {
                        Column newColumn = new Column(column.getColumnName());
                        selectItems.add(new SelectItem<>(newColumn));
                    } else {
                        selectItems.add(selectItem);
                    }
                } else {
                    selectItems.add(selectItem);
                }
                //update-end---author:wangshuai---date:2025-06-05---for:【issues/3802】sprintboot3.3.6集成报表1.9.5，数据库是SQLserver。打开http://localhost:8080/jmreport/list报错，不能显示已有的报表---
            }
        }
        // SELECT *, 1 AS alias FROM TEST
        // 应该为
        // SELECT * FROM (SELECT *, 1 AS alias FROM TEST)
        // 不应该为
        // SELECT *, alias FROM (SELECT *, 1 AS alias FROM TEST)
        for (SelectItem<?> selectItem : selectItems) {
            if (selectItem.getExpression() instanceof AllColumns) {
                return Collections.singletonList(selectItem);
            }
        }
        return selectItems;
    }

    /**
     * 获取 ROW_NUMBER() 列
     *
     * @param plainSelect 原查询
     * @param autoItems   自动生成的查询列
     * @return ROW_NUMBER() 列
     */
    protected SelectItem<?> addRowNumber(PlainSelect plainSelect, List<SelectItem<?>> autoItems) {
        //增加ROW_NUMBER()
        StringBuilder orderByBuilder = new StringBuilder();
        orderByBuilder.append("ROW_NUMBER() OVER (");
        if (isNotEmptyList(plainSelect.getOrderByElements())) {
            orderByBuilder.append(PlainSelect.orderByToString(
                    getOrderByElements(plainSelect, autoItems)).substring(1));
            //清空排序列表
            plainSelect.setOrderByElements(null);
        } else {
            orderByBuilder.append("ORDER BY RAND()");
        }
        orderByBuilder.append(") ");
        orderByBuilder.append(PAGE_ROW_NUMBER);
        return new SelectItem<>(new Column(orderByBuilder.toString()));
    }

    /**
     * 处理selectBody去除Order by
     *
     * @param selectBody
     */
    protected void processSelectBody(Select selectBody, int level) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody, level + 1);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (JSqlSubSelectBody49.getItemSelectBody(withItem) != null) {
                processSelectBody(JSqlSubSelectBody49.getItemSelectBody(withItem), level + 1);
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<Select> plainSelects = operationList.getSelects();
                for (Select plainSelect : plainSelects) {
                    processSelectBody(plainSelect, level + 1);
                }
            }
        }
    }

    /**
     * 处理PlainSelect类型的selectBody
     *
     * @param plainSelect
     */
    protected void processPlainSelect(PlainSelect plainSelect, int level) {
        if (level > 1) {
            if (isNotEmptyList(plainSelect.getOrderByElements())) {
                if (plainSelect.getTop() == null) {
                    plainSelect.setTop(TOP100_PERCENT);
                }
            }
        }
        if (plainSelect.getFromItem() != null) {
            processFromItem(plainSelect.getFromItem(), level + 1);
        }
        if (plainSelect.getJoins() != null && plainSelect.getJoins().size() > 0) {
            List<Join> joins = plainSelect.getJoins();
            for (Join join : joins) {
                if (join.getRightItem() != null) {
                    processFromItem(join.getRightItem(), level + 1);
                }
            }
        }
    }

    /**
     * 处理子查询
     *
     * @param fromItem
     */
    protected void processFromItem(FromItem fromItem, int level) {

        if (fromItem instanceof ParenthesedFromItem) {
            ParenthesedFromItem pFromItem = (ParenthesedFromItem) fromItem;
            if ((pFromItem.getJoins() != null && !pFromItem.getJoins().isEmpty())) {
                ParenthesedFromItem subJoin = (ParenthesedFromItem) fromItem;
                for (Join join : subJoin.getJoins()) {
                    if (join.getRightItem() != null) {
                        processFromItem(join.getRightItem(), level + 1);
                    }
                    if (join.isLeft()) {
                        processFromItem(subJoin, level + 1);
                    }
                }
            }
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSelect() != null) {
                Select subSelect = lateralSubSelect.getSelect();
                processSelectBody(subSelect, level + 1);
            }
        } else if (fromItem instanceof ParenthesedSelect) {
            ParenthesedSelect pFromItem = (ParenthesedSelect) fromItem;
            PlainSelect subSelect = pFromItem.getPlainSelect();
            processSelectBody(subSelect, level + 1);
        }
        //Table时不用处理
    }

    /**
     * List不空
     *
     * @param list
     * @return
     */
    public boolean isNotEmptyList(List<?> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 复制 OrderByElement
     *
     * @param orig  原 OrderByElement
     * @param alias 新 OrderByElement 的排序要素
     * @return 复制的新 OrderByElement
     */
    protected OrderByElement cloneOrderByElement(OrderByElement orig, String alias) {
        return cloneOrderByElement(orig, new Column(alias));
    }

    /**
     * 复制 OrderByElement
     *
     * @param orig       原 OrderByElement
     * @param expression 新 OrderByElement 的排序要素
     * @return 复制的新 OrderByElement
     */
    protected OrderByElement cloneOrderByElement(OrderByElement orig, Expression expression) {
        OrderByElement element = new OrderByElement();
        element.setAsc(orig.isAsc());
        element.setAscDescPresent(orig.isAscDescPresent());
        element.setNullOrdering(orig.getNullOrdering());
        element.setExpression(expression);
        return element;
    }

    /**
     * 获取新的排序列表
     *
     * @param plainSelect 原始查询
     * @param autoItems   生成的新查询要素
     * @return 新的排序列表
     */
    protected List<OrderByElement> getOrderByElements(PlainSelect plainSelect, List<SelectItem<?>> autoItems) {

        // 非 `*` 且 非 `t.*` 查询列集合
        Map<String, SelectItem<?>> selectMap = new HashMap<>();
        // 别名集合
        Set<String> aliases = new HashSet<>();
        // 是否包含 `*` 查询列
        boolean allColumns = false;
        // `t.*` 查询列的表名集合
        Set<String> allColumnsTables = new HashSet<>();

        for (SelectItem<?> item : plainSelect.getSelectItems()) {
            Expression expression = item.getExpression();
            if (expression instanceof AllTableColumns) {
                allColumnsTables.add(((AllTableColumns) expression).getTable().getName());
            } else if (expression instanceof AllColumns) {
                allColumns = true;
            } else {
                selectMap.put(expression.toString(), item);
                Alias alias = item.getAlias();
                if (alias != null) {
                    aliases.add(alias.getName());
                }
            }
        }

        // 开始遍历 OrderByElement 列表
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        ListIterator<OrderByElement> iterator = orderByElements.listIterator();
        int aliasNo = 1;
        OrderByElement orderByElement;
        while (iterator.hasNext()) {
            orderByElement = iterator.next();
            Expression expression = orderByElement.getExpression();
            SelectItem<?> selectExpressionItem = selectMap.get(expression.toString());
            if (selectExpressionItem != null) { // OrderByElement 在查询列表中
                Alias alias = selectExpressionItem.getAlias();
                if (alias != null) { // 查询列含有别名时用查询列别名
                    iterator.set(cloneOrderByElement(orderByElement, alias.getName()));

                } else { // 查询列不包含别名
                    if (expression instanceof Column) {
                        // 查询列为普通列，这时因为列在嵌套查询外时名称中不包含表名，故去除排序列的表名引用
                        // 例（仅为解释此处逻辑，不代表最终分页结果）：
                        // SELECT TEST.A FROM TEST ORDER BY TEST.A
                        // -->
                        // SELECT A FROM (SELECT TEST.A FROM TEST) ORDER BY A
                        ((Column) expression).setTable(null);

                    } else {
                        // 查询列不为普通列时（例如函数列）不支持分页
                        // 此种情况比较难预测，简单的增加新列容易产生不可预料的结果
                        // 而为列增加别名是非常简单的，故此要求排序复杂列必须使用别名
                        throw new PageException("列 \"" + expression + "\" 需要定义别名");
                    }
                }

            } else { // OrderByElement 不在查询列表中，需要自动生成一个查询列
                if (expression instanceof Column) { // OrderByElement 为普通列
                    Table table = ((Column) expression).getTable();
                    if (table == null) { // 表名为空
                        if (allColumns ||
                                (allColumnsTables.size() == 1 && plainSelect.getJoins() == null) ||
                                aliases.contains(((Column) expression).getColumnName())) {
                            // 包含`*`查询列 或者 只有一个 `t.*`列且为单表查询 或者 其实排序列是一个别名
                            // 此时排序列其实已经包含在查询列表中了，不需做任何操作
                            continue;
                        }

                    } else { //表名不为空
                        String tableName = table.getName();
                        if (allColumns || allColumnsTables.contains(tableName)) {
                            // 包含`*`查询列 或者 包含特定的`t.*`列
                            // 此时排序列其实已经包含在查询列表中了，只需去除排序列的表名引
                            ((Column) expression).setTable(null);
                            continue;
                        }
                    }
                }

                // 将排序列加入查询列中
                String aliasName = PAGE_COLUMN_ALIAS_PREFIX + aliasNo++;

                SelectItem<Expression> item = new SelectItem<>();
                item.setExpression(expression);
                item.setAlias(new Alias(aliasName));
                autoItems.add(item);

                iterator.set(cloneOrderByElement(orderByElement, aliasName));
            }
        }

        return orderByElements;
    }
}
