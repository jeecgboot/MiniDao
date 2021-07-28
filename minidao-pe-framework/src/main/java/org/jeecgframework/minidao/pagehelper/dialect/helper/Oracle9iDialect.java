package org.jeecgframework.minidao.pagehelper.dialect.helper;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 * Oracle9i
 */
public class Oracle9iDialect extends AbstractHelperDialect {


    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        sqlBuilder.append("SELECT * FROM ( ");
        sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM PAGEHELPER_ROW_ID FROM ( \n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n ) TMP_PAGE WHERE ROWNUM <= {2} ");
        sqlBuilder.append(" ) WHERE PAGEHELPER_ROW_ID > {0} ");
        String newSql = sqlBuilder.toString();
        newSql = super.format(newSql, super.getPageParam(miniDaoPage));
        return newSql;
    }

}
