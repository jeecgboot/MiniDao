package org.jeecgframework.minidao.annotation.type;

/**
 * 主键策略
 */
public enum IdType {

    AUTO("native"), //自增
    ID_WORKER("id_worker"),//分布式ID
    UUID("uuid");//uuid

    private String value;
    public String getValue() {
        return value;
    }
    private IdType(String value) {
        this.value = value;
    }
}
