package org.jeecgframework.minidao.sqlparser.impl.vo;

import java.util.HashSet;
import java.util.Set;

public class QueryTable {
    //数据库名
    private String dbName;
    //表名
    private String name;
    //表的别名
    private String alias;
    // 字段名集合
    private Set<String> fields;
    // 是否查询所有字段
    private boolean all;

    public QueryTable() {
    }

    public QueryTable(String name, String alias) {
        this.name = name;
        this.alias = alias;
        this.all = false;
        this.fields = new HashSet<>();
    }

    public QueryTable(String dbName, String name, String alias) {
        this.dbName = dbName;
        this.name = name;
        this.alias = alias;
        this.all = false;
        this.fields = new HashSet<>();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    public Set<String> getFields() {
        return new HashSet<>(fields);
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public boolean isAll() {
        return all;
    }

}
