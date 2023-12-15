package org.jeecgframework.minidao.pagehelper.dialect;

import org.jeecgframework.minidao.pojo.MiniDaoPage;


/**
 * 数据库方言，针对不同数据库进行实现
 */
public interface Dialect {

    /**
     * 生成分页查询 sql
     *
     * @param sql
     * @param pageSetting
     * @return
     */
    String getPageSql(String sql, MiniDaoPage pageSetting);

    /**
     * 获取分页参数
     *
     * @param miniDaoPage
     * @return
     */
    String[] getPageParam(MiniDaoPage miniDaoPage);
}
