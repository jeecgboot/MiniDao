package org.jeecgframework.minidao.pagehelper.dialect;

import org.jeecgframework.minidao.pojo.MiniDaoPage;


/**
 * 针对 PageHelper 的实现
 *
 */
public abstract class AbstractHelperDialect extends AbstractDialect {

    /**
     * 单独处理分页部分
     *
     * @param sql
     * @param pageSetting
     * @return
     */
    @Override
    public abstract String getPageSql(String sql, MiniDaoPage pageSetting);

    /**
     * 格式化占位参数
     *
     * @param sql
     * @param sqlParam
     * @return
     */
    protected String format(String sql, String... sqlParam) {
        int i = 0;
        if (sql != null && sqlParam != null && sqlParam.length > 0) {
            for (String s : sqlParam) {
                sql = sql.replace("{" + i + "}", s);
                i++;
            }
        }
        return sql;
    }

}
