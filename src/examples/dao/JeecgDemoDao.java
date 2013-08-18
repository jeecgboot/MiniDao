package examples.dao;

import org.jeecgframework.minidao.annotation.MiniDaoEntity;
import org.jeecgframework.minidao.hibernate.MiniDaoSupportHiber;

import examples.entity.JeecgDemo;



/**
 * 传入参数：支持多种类型 List<T>\Map\Object\基本类型
 * @param po
 */
@MiniDaoEntity(JeecgDemo.class)
public interface JeecgDemoDao extends MiniDaoSupportHiber<JeecgDemo>{

}
