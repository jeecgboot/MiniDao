package org.jeecgframework.minidao.pagehelper.dialect.nosql;

import org.jeecgframework.minidao.pagehelper.dialect.AbstractHelperDialect;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.List;

/**
 * 数据库差异：presto数据库,注意presto 不支持jdbc的Preparedtatment 以及不支持offset
 *
 * @author xiandafu
 */
public class PrestoDialect extends AbstractHelperDialect {

    @Override
    public String getPageSql(String sql, MiniDaoPage miniDaoPage) {
        throw new UnsupportedOperationException("Presto 翻页查询需要手工sql完成，无法辅助自动生成");
    }

}
