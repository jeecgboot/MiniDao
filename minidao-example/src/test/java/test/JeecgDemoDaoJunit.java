package test;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.spring.SpringTxTestCase;
import examples.dao.JeecgDemoDao;
import examples.entity.JeecgDemo;
import examples.service.JeecgDemoService;

/**
 * 单元测试
 * MiniDao支持实体维护
 * 
 * @author yanping.shi
 * 
 */
public class JeecgDemoDaoJunit extends SpringTxTestCase {

	@Autowired
	JeecgDemoService jeecgDemoService;
	
	String  id = "402880e74a2f2fd8014a2f2fdb1b0000";
	
	
	//@Test
	public void testInsert() {
		JeecgDemo jeecgDemo = new JeecgDemo();
		jeecgDemo.setAge(30);
		jeecgDemo.setBirthday(new Date());
		jeecgDemo.setUserName("小明的数学");
		jeecgDemoService.saveByHiber(jeecgDemo);
		id = jeecgDemo.getId();
	}

	@Test
	public void testUpdate() {
		JeecgDemo jeecgDemo = new JeecgDemo();
		jeecgDemo.setId(id);
		jeecgDemo.setAge(90);
		jeecgDemo.setBirthday(new Date());
		jeecgDemo.setUserName("李四");
		jeecgDemoService.updateByHiber(jeecgDemo);
	}


	//@Test
	public void testGetEntity() {
		JeecgDemo jeecgDemo = jeecgDemoService.getByIdHiber(JeecgDemo.class,id);
		if(jeecgDemo!=null){
			logger.info("-------------testGetEntity() -------------"+jeecgDemo.getAge()+"       "+jeecgDemo.getUserName());
		}
	}

	//@Test
	public void testDelete() {
		JeecgDemo jeecgDemo = new JeecgDemo();
		//删除必须带上ID
		jeecgDemo.setId(id);
		jeecgDemoService.deleteByHiber(jeecgDemo);
	}
	
	
	//@Test
	public void testDeleteById() {
		jeecgDemoService.deleteByIdHiber(JeecgDemo.class, id);
	}
	

	//@Test
	public void testListAll() {
		JeecgDemo jeecgDemo = new JeecgDemo();
		List<JeecgDemo> list = jeecgDemoService.listByHiber(jeecgDemo);
		for(JeecgDemo po:list){
			logger.info(po.getUserName());
		}
	}

}
