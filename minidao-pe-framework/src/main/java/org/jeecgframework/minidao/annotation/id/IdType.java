package org.jeecgframework.minidao.annotation.id;

/**
 * 主键类型
 */
public enum IdType {
    UUID(0),
    ID_WORKER(1),
    AUTO(2),
    ID_SEQ(3);

    private final int key;

    private IdType(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

}
