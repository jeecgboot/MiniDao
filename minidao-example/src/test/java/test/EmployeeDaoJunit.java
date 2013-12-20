package test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.spring.SpringTxTestCase;
import examples.dao.EmployeeDao;
import examples.entity.Employee;

/**
 * 单元测试
 * 
 * @author yanping.shi
 * 
 */
public class EmployeeDaoJunit extends SpringTxTestCase {

	@Autowired
	private EmployeeDao employeeDao;
	
	int maxNum ;
	
	
	@Before
	public void testGetCount() {
		maxNum = employeeDao.getCount();
		logger.info("当前数据条数 --------------------------("+maxNum+")");
	}
	
	@Test
	public void testInsert() {
		logger.info("--------testInsert--------------------------------------------------------------");

		Employee employee = new Employee();
		employee.setId(new Integer(maxNum+1).toString());
		employee.setName("scott");
		employee.setBirthday(new Date());
		employee.setAge(20);
	    employeeDao.insert(employee);
	}

	//@Test
	public void testUpdate() {
		logger.info("--------testUpdate--------------------------------------------------------------");

		Employee employee = new Employee();
		employee.setId("2");
		employee.setName("张代浩的世界1");
		employee.setBirthday(new Date());
		int num = employeeDao.update(employee);
		logger.info("------update---count---"+num);
	}

	//@Test
	public void testGetMap() {
		logger.info("--------testGetMap--------------------------------------------------------------");

		//如果没有数据获取报错
		Map<String, Object> mp = employeeDao.getMap("001","张开忠");
		logger.info(mp.get("id"));
		logger.info(mp.get("name"));
		logger.info(mp.get("empno"));
		logger.info(mp.get("age"));
		logger.info(mp.get("birthday"));
		logger.info(mp.get("salary"));
	}

	//@Test
	public void testGetEntity() {
		Employee employee = employeeDao.getEmployee("001");
		logger.info("testGetEntity --" +employee.getName());
	}

	//@Test
	public void testListAll() {
		logger.info("--------testListAll--------------------------------------------------------------");
		Employee employee = new Employee();
		List<Map> list =  employeeDao.getAllEmployees(employee);
		for(Map mp:list){
			logger.info(mp.get("id"));
			logger.info(mp.get("name"));
			logger.info(mp.get("empno"));
			logger.info(mp.get("age"));
			logger.info(mp.get("birthday"));
			logger.info(mp.get("salary"));
		}
	}
	

}
