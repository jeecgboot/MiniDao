package org.jeecgframework.minidao.pagehelper.dialect.nosql;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.text.MessageFormat;

/**
 *  Hive 【大数据】
 *  遗留问题，hive的子查询结果，比如 select * from (select id from c) t,jdbc返回结果应该是id，但他返回了t.id
 *  因此HiveStyle需要重新实现BeanProcessor#getColName方法，返回正确的列名.
 *  因此不排除用户根据自己需要来重新实现getColName方法
 */
public class HiveDialect extends AbstractHelperDialect {
    static String pagePrefix = "page_";
    /*需要一个排序字段 id,否则不能翻页*/
    String jdbcPageTemplate ="select * from \n" +
            "        (\n" +
            "        SELECT ccc.*,ROW_NUMBER() over (Order by ccc.id) as rowid FROM ({0}) ccc \n" +
            "        ) "+pagePrefix+" \n" +
            "    where rowid > {1} and rowid <={2}";

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        String[] pageParam = super.getPageParam(miniDaoPage);
        String newSql = super.format(jdbcPageTemplate,sql,pageParam[0],pageParam[1]);
        return newSql;
    }
}
