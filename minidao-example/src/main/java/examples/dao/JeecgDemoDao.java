package examples.dao;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.hibernate.MiniDaoSupportHiber;

import examples.entity.JeecgDemo;



/**
 * 传入参数：支持多种类型 List<T>\Map\Object\基本类型
 * @param po
 */
@MiniDao
public interface JeecgDemoDao extends MiniDaoSupportHiber<JeecgDemo>{

	@Sql("SELECT count(*) FROM user")
    Integer getCount();
}
