package test;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import examples.dao.EmployeeDao;
import examples.entity.Employee;

import test.spring.SpringTxTestCase;

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
		System.out.println("当前数据条数 --------------------------("+maxNum+")");
	}
	
	@Test
	public void testInsert() {
		System.out.println("--------testUpdate--------------------------------------------------------------");

		Employee employee = new Employee();
		employee.setId(new Integer(maxNum+1).toString());
		employee.setName("张开忠2");
		 employeeDao.insert(employee);
		System.out.println("--------testUpdate--------------------------------------------------------------");
	}

	@Test
	public void testUpdate() {
		System.out.println("--------testUpdate--------------------------------------------------------------");

		Employee employee = new Employee();
		employee.setId("1");
		employee.setName("张开忠");
		int num = employeeDao.update(employee);
		System.out.println("------update---count---"+num);
		System.out.println("--------testUpdate--------------------------------------------------------------");
	}

	//@Test
	public void testGetMap() {
		System.out.println("--------testGetMap--------------------------------------------------------------");

		//如果没有数据获取报错
		Map<String, Object> mp = employeeDao.getMap("001","张开忠");
		System.out.println(mp.get("id"));
		System.out.println(mp.get("name"));
		System.out.println(mp.get("empno"));
		System.out.println(mp.get("age"));
		System.out.println(mp.get("birthday"));
		System.out.println(mp.get("salary"));
		System.out.println("--------testGetMap--------------------------------------------------------------");
	}

	@Test
	public void testGetEntity() {
		Employee employee = employeeDao.getEmployee("001");
		System.out.println("testGetEntity --" +employee.getName());
	}

	@Test
	public void testListAll() {
		System.out.println("--------testListAll--------------------------------------------------------------");
		Employee employee = new Employee();
		List<Map> list =  employeeDao.getAllEmployees(employee);
		for(Map mp:list){
			System.out.println(mp.get("id"));
			System.out.println(mp.get("name"));
			System.out.println(mp.get("empno"));
			System.out.println(mp.get("age"));
			System.out.println(mp.get("birthday"));
			System.out.println(mp.get("salary"));
		}
		System.out.println("--------testListAll--------------------------------------------------------------");
	}
	

}
