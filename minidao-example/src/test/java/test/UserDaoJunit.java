package test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.spring.SpringTxTestCase;
import examples.dao.user.UserDao;
import examples.entity.user.User;

/**
 * 单元测试
 * MiniDao支持实体维护
 * 
 * @author yanping.shi
 * 
 */
public class UserDaoJunit extends SpringTxTestCase {

	@Autowired
	private UserDao userDao;
	
	String  id = "402880e740a648990140a6489c470000";
	
	
	//@Test
	public void testInsert() {
		User user = new User();
		user.setName("张代浩2");
		user.setAge(22);
		
		userDao.saveByHiber(user);
	}

	//@Test
	public void testUpdate() {
		User user = new User();
		user.setName("小张");
		user.setAge(20);
		user.setId(id);
		userDao.updateByHiber(user);
	}


	//@Test
	public void testGetEntity() {
		User user = userDao.getByIdHiber(User.class, id);
		System.out.println("---------------------------");
		System.out.println(user.getName());
		System.out.println(user.getAge());
		System.out.println(user.getBirthday());
	}

	//@Test
	public void testDelete() {
	userDao.deleteByIdHiber(User.class, id);
	}
	
	
	//@Test
	public void testDeleteById() {
	}

	//@Test
	public void testListAll() {
		User user = new User();
		user.setName("小张");
		List<User> list = userDao.listByHiber(user);
		for(User u:list){
			System.out.println(u.getName());
			System.out.println(u.getAge());
		}
	}
	
	//@Test
	public void testSql() {
		Integer age = new Integer(30);
		List<Map> list =  userDao.listUserByAge(30);
		System.out.println("------------------------------------------------------");
		System.out.println("小于30岁人的名字");
		for(Map m:list){
			System.out.println(m.get("name"));
		}
	
	}
	
	//@Test
	public void testUpSql() {
		Integer age = new Integer(20);
		userDao.updateUserBirthday("小张", age, new Date());
	
	}
	
	
	@Test
	public void testsle() {
		String s = userDao.sle();
	
	}

}
