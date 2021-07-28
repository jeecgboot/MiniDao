package org.jeecgframework.minidao.pagehelper;

/**
 * 分页插件异常处理类
 */
public class PageException extends RuntimeException {
    public PageException() {
        super();
    }

    public PageException(String message) {
        super(message);
    }

    public PageException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageException(Throwable cause) {
        super(cause);
    }
}
