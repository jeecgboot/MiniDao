package test;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import examples.dao.EmployeeDao;
import examples.entity.Employee;

/**
 * 测试自增ID（注意：测试该方法需要把数据库ID类型改成int，自增）
 */
public class ClientDaoIdNative {
	public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		EmployeeDao employeeDao = (EmployeeDao) factory.getBean("employeeDao");
		
		Employee employee = new Employee();
		employee.setEmpno("200");
		employee.setName("scott");
		employee.setBirthday(new Date());
		employee.setAge(20);
		employee.setSalary(new BigDecimal(88888));
		int i = employeeDao.insertNative(employee);
		System.out.println("主键："+i);
	}
}
