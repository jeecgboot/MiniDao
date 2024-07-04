package org.jeecgframework.minidao.pagehelper.dialect;

import org.jeecgframework.minidao.pojo.MiniDaoPage;

/**
 * 基于 CountSqlParser 的智能 Count 查询
 *
 */
public abstract class AbstractDialect implements Dialect {

    /**
     * 处理分页参数
     * @param miniDaoPage
     * @return
     */
    @Override
    public String[] getPageParam(MiniDaoPage miniDaoPage){
        int page = miniDaoPage.getPage();
        int rows = miniDaoPage.getRows();
        //TODO 拿出去按照数据库重新计算
        int beginIndex = (page - 1) * rows;
        int endIndex = beginIndex + rows;;
        String[] sqlParam = new String[3];
        sqlParam[0] = beginIndex + "";
        sqlParam[1] = rows + "";
        sqlParam[2] = endIndex+"";
        return sqlParam;
    }
}
