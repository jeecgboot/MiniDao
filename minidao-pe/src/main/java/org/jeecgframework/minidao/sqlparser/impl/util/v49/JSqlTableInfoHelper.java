//package org.jeecgframework.minidao.sqlparser.impl.util.v49;
//
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.DoubleValue;
//import net.sf.jsqlparser.expression.Expression;
//import net.sf.jsqlparser.expression.LongValue;
//import net.sf.jsqlparser.expression.StringValue;
//import net.sf.jsqlparser.parser.CCJSqlParserManager;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.*;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
//
//import java.io.StringReader;
//import java.util.*;
//
//public class JSqlTableInfoHelper {
//    private static final Log logger = LogFactory.getLog(JSqlTableInfoHelper.class);
//
//    /**
//     * key-alias
//     * value-QueryTable
//     */
//    private static final ThreadLocal<Map<String, QueryTable>> threadLocalMap = new ThreadLocal<>();
//
//    /**
//     * 主表名
//     */
//    private static final ThreadLocal<String> threadLocalMainTableAlias = new ThreadLocal<>();
//
//    private static JSqlTableInfoHelper instance = null;
//
//    /**
//     * 初始化本地变量
//     */
//    private void init() {
//        threadLocalMap.set(new HashMap<>(5));
//        threadLocalMainTableAlias.set("");
//    }
//
//    /**
//     * 清除本地变量
//     */
//    private void destroy() {
//        threadLocalMap.remove();
//        threadLocalMainTableAlias.remove();
//    }
//
//    /**
//     * 添加一个表信息
//     */
//    private void addQueryTable(String alias, QueryTable queryTable) {
//        threadLocalMap.get().put(alias, queryTable);
//    }
//
//    /**
//     * 获取一个表信息
//     */
//    private QueryTable getQueryTable(String alias) {
//        return threadLocalMap.get().get(alias);
//    }
//
//    /**
//     * 往一个表信息中添加字段
//     */
//    private void addQueryTableField(String key, String field) {
//        QueryTable queryTable = threadLocalMap.get().get(key);
//        queryTable.addField(field);
//    }
//
//    /**
//     * 返回结果-并清除变量
//     */
//    private List<QueryTable> getResult() {
//        Map<String, QueryTable> localMap = threadLocalMap.get();
//        ArrayList<QueryTable> list = new ArrayList<>(localMap.values());
//        destroy();
//        return list;
//    }
//
//    public static List<QueryTable> getQueryTableInfo(String sql) {
//        if (JSqlTableInfoHelper.instance == null) {
//            JSqlTableInfoHelper.instance = new JSqlTableInfoHelper();
//        }
//        return JSqlTableInfoHelper.instance.getQueryTableInfoIns(sql);
//    }
//
//    public List<QueryTable> getQueryTableInfoIns(String sql) {
//        init();
//        CCJSqlParserManager parser = new CCJSqlParserManager();
//        try {
//            Statement stmt = parser.parse(new StringReader(sql));
//            if (stmt instanceof Select) {
//                Select selectBody = (Select) stmt;
//                // 普通查询语句
//                if (selectBody instanceof PlainSelect) {
//                    PlainSelect plainSelect = (PlainSelect) selectBody;
//                    handleTable(plainSelect);
//                    handleColumn(plainSelect);
//                }
//                // union查询
//                if (selectBody instanceof SetOperationList) {
//                    SetOperationList selectBodyList = (SetOperationList) selectBody;
//                    List<Select> selects = selectBodyList.getSelects();
//                    for (Select temp : selects) {
//                        if (temp instanceof PlainSelect) {
//                            PlainSelect plainSelect = (PlainSelect) temp;
//                            handleTable(plainSelect);
//                            handleColumn(plainSelect);
//                        }
//                    }
//                }
//            }
//            return getResult();
//        } catch (JSQLParserException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    /**
//     * 处理表名
//     */
//    private void handleTable(PlainSelect plainSelect) {
//        // 1、处理 fromItem
//        this.handleFromItem(plainSelect.getFromItem());
//        // 2、获取join的表
//        List<Join> list = plainSelect.getJoins();
//        if (list != null) {
//            for (Join join : list) {
//                Table joinTable = (Table) join.getRightItem();
//                addTableAlias(joinTable);
//            }
//        }
//    }
//
//    /**
//     * 处理 fromItem，可 flat fromItem
//     */
//    private void handleFromItem(FromItem fromItem) {
//        if (fromItem instanceof ParenthesedFromItem) {
//            this.handleFromItem(((ParenthesedFromItem) fromItem).getFromItem());
//        } else if (fromItem instanceof Table) {
//            Table table = (Table) fromItem;
//            addTableAlias(table);
//        } else if (fromItem instanceof Select) {
//            PlainSelect select = ((Select) fromItem).getPlainSelect();
//            handleTable(select);
//            handleColumn(select);
//        } else {
//            logger.error("不支持的类型: " + fromItem.getClass().getName());
//        }
//    }
//
//    /**
//     * 将 表别名-表名 添加到map
//     */
//    private void addTableAlias(Table table) {
//        String alias = "";
//        if (table.getAlias() != null) {
//            alias = table.getAlias().getName();
//        } else {
//            alias = table.getName();
//        }
//        if (threadLocalMainTableAlias.get().isEmpty()) {
//            threadLocalMainTableAlias.set(alias);
//        }
//        //tableMap.put(alias, new QueryTable(table.getName(), alias));
//        addQueryTable(alias, new QueryTable(table.getSchemaName(), table.getName(), alias));
//    }
//
//    /**
//     * 处理列
//     */
//    private void handleColumn(PlainSelect plainSelect) {
//        List<SelectItem<?>> list = plainSelect.getSelectItems();
//        String mainTable = threadLocalMainTableAlias.get();
//        for (SelectItem<?> selectItem : list) {
//            selectItem.accept(new SelectItemVisitorAdapter() {
//                @Override
//                public void visit(SelectItem item) {
//                    // 添加列
//                    Expression exp = item.getExpression();
//                    if (exp instanceof AllTableColumns) {
//                        AllTableColumns columns = (AllTableColumns) exp;
//                        // select t.* from tablet t
//                        String alias = null;
//                        try {
//                            alias = columns.getTable().getName();
//                        } catch (Exception ignored) {
//                        }
//                        if (alias == null) {
//                            alias = mainTable;
//                        }
//                        //tableMap.get(alias).setAll(true);
//                        getQueryTable(alias).setAll(true);
//
//                    } else if (exp instanceof AllColumns) {
//                        AllColumns columns = (AllColumns) exp;
//                        // select * from table
//                        if ("*".equals(columns.toString())) {
//                            // tableMap.get(mainTable).setAll(true);
//                            getQueryTable(mainTable).setAll(true);
//                        }
//
//                    } else if (exp instanceof Column) {
//                        // 如果查询出来的是 确定的列 获取表的别名 a.name 获取a
//                        Column c = (Column) exp;
//                        if (c.getTable() == null) {
//                            // select name from table
//                            String str = c.getColumnName();
//                            //tableMap.get(mainTable).addField(str);
//                            addQueryTableField(mainTable, str);
//                        } else {
//                            // select t.name from table t
//                            String tableAlias = c.getTable().getName();
//                            QueryTable queryTable = null;
//                            //找到queryTable
//                            if (tableAlias == null || tableAlias.isEmpty()) {
//                                //没有别名 认为他是mainTable的字段，无奈之举
//                                //queryTable = tableMap.get(mainTable);
//                                queryTable = getQueryTable(mainTable);
//                            } else {
//                                //queryTable = tableMap.get(tableAlias);
//                                queryTable = getQueryTable(tableAlias);
//                            }
//                            if (queryTable != null) {
//                                // 出现null的情况是 select x.name from table y
//                                queryTable.addField(c.getColumnName());
//                            }
//                        }
//                    } else if (exp instanceof Select) {
//                        // 处理子查询
//                        Select select = (Select) exp;
//                        PlainSelect selectBody = select.getPlainSelect();
//                        handleTable(selectBody);
//                        handleColumn(selectBody);
//                    } else if (isSimpleValue(exp)) {
//                        // 如果查询出来的是 固定的值  不做处理
//                    } else {
//                        // 函数什么的--
//                        String str = exp.toString();
//                        boolean isAdded = false;
//                        Set<String> keySet = threadLocalMap.get().keySet();
//                        for (String alias : keySet) {
//                            String temp = alias + ".";
//                            if (str.contains(temp)) {
//                                isAdded = true;
//                                // tableMap.get(alias).addField(str);
//                                addQueryTableField(alias, str);
//                            }
//                        }
//                        if (!isAdded) {
//                            // tableMap.get(mainTable).addField(str);
//                            addQueryTableField(mainTable, str);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 字面量不做处理
//     */
//    private boolean isSimpleValue(Expression exp) {
//        return exp instanceof StringValue || exp instanceof DoubleValue || exp instanceof LongValue;
//    }
//
//}
