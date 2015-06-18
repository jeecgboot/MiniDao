package test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.spring.SpringTxTestCase;
import examples.entity.user.User;
import examples.service.UserServer;

/**
 * 单元测试
 * MiniDao支持实体维护
 * 
 * @author yanping.shi
 * 
 */
public class UserDaoJunit extends SpringTxTestCase {

	@Autowired
	private UserServer userServer;
	
	String  id = "402880e74a2f3678014a2f367b790000";
	
	//@Test
	public void testInsert() {
		User user = new User();
		user.setName("张代浩2");
		user.setAge(22);
		
		userServer.saveByHiber(user);
	}

	//@Test
	public void testUpdate() {
		User user = new User();
		user.setName("小张");
		user.setAge(20);
		user.setId(id);
		userServer.updateByHiber(user);
	}


	//@Test
	public void testGetEntity() {
		User user = userServer.getByIdHiber(User.class, id);
		System.out.println("---------------------------");
		System.out.println(user.getName());
		System.out.println(user.getAge());
		System.out.println(user.getBirthday());
	}

	//@Test
	public void testDelete() {
	userServer.deleteByIdHiber(User.class, id);
	}
	
	
	//@Test
	public void testDeleteById() {
	}

	//@Test
	public void testListAll() {
		User user = new User();
		user.setName("小张");
		List<User> list = userServer.listByHiber(user);
		for(User u:list){
			System.out.println(u.getName());
			System.out.println(u.getAge());
		}
	}
	
	//@Test
	public void testSql() {
		Integer age = new Integer(30);
		List<Map<String, Object>> list =  userServer.listUserByAge(30);
		System.out.println("------------------------------------------------------");
		System.out.println("小于30岁人的名字");
		for(Map m:list){
			System.out.println(m.get("name"));
		}
	
	}
	
	//@Test
	public void testUpSql() {
		Integer age = new Integer(20);
		userServer.updateUserBirthday(null, null, new Date());
	
	}
	@Test
	public void testDynamicUpdateSql() {
		Integer age = new Integer(20);
		userServer.updateDynamicUserBirthday("小张", null, new Date());

	}

	
	@Test
	public void testsle() {
		//String s = userServer.sle();
	
	}

}
