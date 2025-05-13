//package org.jeecgframework.minidao.sqlparser.impl.util;
//
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.*;
//import net.sf.jsqlparser.parser.CCJSqlParserManager;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.*;
//import org.jeecgframework.minidao.sqlparser.impl.vo.QueryTable;
//
//import java.io.StringReader;
//import java.util.*;
//
//public class JSqlTableInfoHelper {
//
//    private static JSqlTableInfoHelper instance = null;
//
//    /**
//     * key-alias
//     * value-QueryTable
//     */
//    private static ThreadLocal<Map<String, QueryTable>> threadLocalMap = new ThreadLocal<>();
//
//    /**
//     * 主表名
//     */
//    private static ThreadLocal<String> threadLocalMainTableAlias = new ThreadLocal<>();
//
//
//    /**
//     * 初始化本地变量
//     */
//    private void init() {
//        threadLocalMap.set(new HashMap(5));
//        threadLocalMainTableAlias.set(new String());
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
//        if (queryTable == null) {
//            queryTable = new QueryTable(key, key);
//            threadLocalMap.get().put(key, queryTable);
//        }
//        queryTable.addField(field);
//    }
//
//    /**
//     * 返回结果-并清除变量
//     */
//    private List<QueryTable> getResult() {
//        Map<String, QueryTable> localMap = threadLocalMap.get();
//        ArrayList<QueryTable> list = new ArrayList<QueryTable>(localMap.values());
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
//                Select selectStatement = (Select) stmt;
//                SelectBody selectBody = selectStatement.getSelectBody();
//                // 普通查询语句
//                if (selectBody instanceof PlainSelect) {
//                    PlainSelect plainSelect = (PlainSelect) selectBody;
//                    handleTable(plainSelect);
//                    handleColumn(plainSelect);
//                }
//                // union查询
//                if (selectBody instanceof SetOperationList) {
//                    SetOperationList selectBodyList = (SetOperationList) selectBody;
//                    List<SelectBody> selects = selectBodyList.getSelects();
//                    for (int i = 0; i < selects.size(); i++) {
//                        SelectBody temp = selects.get(i);
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
//    /**
//     * 处理表名
//     *
//     * @param plainSelect
//     */
//    private void handleTable(PlainSelect plainSelect) {
//        FromItem fromItem = plainSelect.getFromItem();
//        if (fromItem instanceof Table) {
//            Table table = (Table) plainSelect.getFromItem();
//            addTableAlias(table);
//            // 获取join的表
//            List<Join> list = plainSelect.getJoins();
//            if (list != null) {
//                for (Join join : list) {
//                    Table joinTable = (Table) join.getRightItem();
//                    addTableAlias(joinTable);
//                }
//            }
//        } else if (fromItem instanceof SubSelect) {
//            SelectBody select = ((SubSelect) fromItem).getSelectBody();
//            if (select instanceof PlainSelect) {
//                PlainSelect subSelect = (PlainSelect) select;
//                handleTable(subSelect);
//                handleColumn(subSelect);
//            }
//        }
//    }
//
//    /**
//     * 将 表别名-表名 添加到map
//     *
//     * @param table
//     */
//    private void addTableAlias(Table table) {
//        String alias = "";
//        if (table.getAlias() != null) {
//            alias = table.getAlias().getName();
//        } else {
//            alias = table.getName();
//        }
//        if (threadLocalMainTableAlias.get().length() == 0) {
//            threadLocalMainTableAlias.set(alias);
//        }
//        //tableMap.put(alias, new QueryTable(table.getName(), alias));
//        addQueryTable(alias, new QueryTable(table.getName(), alias));
//    }
//
//    /**
//     * 处理列
//     *
//     * @param plainSelect
//     */
//    private void handleColumn(PlainSelect plainSelect) {
//        List<SelectItem> list = plainSelect.getSelectItems();
//        String mainTable = threadLocalMainTableAlias.get();
//        for (SelectItem selectItem : list) {
//            selectItem.accept(new SelectItemVisitorAdapter() {
//                @Override
//                public void visit(SelectExpressionItem item) {
//                    // 添加列
//                    Expression exp = item.getExpression();
//                    if (exp instanceof Column) {
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
//                            if (tableAlias == null || "".equals(tableAlias)) {
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
//
//                    } else if (exp instanceof SubSelect) {
//                        SelectBody selectBody = ((SubSelect) exp).getSelectBody();
//                        if (selectBody instanceof PlainSelect) {
//                            PlainSelect subSelect = (PlainSelect) selectBody;
//                            // 处理子查询
//                            handleTable(subSelect);
//                            handleColumn(subSelect);
//                        }
//                    } else if (isSimpleValue(exp)) {
//                        // 如果查询出来的是 固定的值  不做处理
//                    } else {
//                        // 函数什么的--
//                        String str = exp.toString();
//                        boolean isAdded = false;
//                        Set<String> keySet = threadLocalMap.get().keySet();
//                        for (String alias : keySet) {
//                            String temp = alias + ".";
//                            if (str.indexOf(temp) >= 0) {
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
//
//                @Override
//                public void visit(AllTableColumns columns) {
//                    // select t.* from tablet t
//                    String alias = null;
//                    try {
//                        alias = columns.getTable().getName();
//                    } catch (Exception e) {
//                    }
//                    if (alias == null) {
//                        alias = mainTable;
//                    }
//                    //tableMap.get(alias).setAll(true);
//                    getQueryTable(alias).setAll(true);
//                }
//
//                @Override
//                public void visit(AllColumns columns) {
//                    // select * from table
//                    if ("*".equals(columns.toString())) {
//                        // tableMap.get(mainTable).setAll(true);
//                        getQueryTable(mainTable).setAll(true);
//                    }
//                }
//            });
//        }
//    }
//
//    // 函数-concat-cast-case--穷举不尽还有自己定义的 没法处理全,放弃 可TODO
//    private boolean isFunction(Expression exp) {
//        if (exp != null) {
//            return exp instanceof Function || exp instanceof BinaryExpression || exp instanceof CastExpression || exp instanceof CaseExpression;
//        }
//        return false;
//    }
//
//
//    /**
//     * 字面量不做处理
//     *
//     * @return
//     */
//    private boolean isSimpleValue(Expression exp) {
//        return exp instanceof StringValue || exp instanceof DoubleValue || exp instanceof LongValue;
//    }
//
//}
