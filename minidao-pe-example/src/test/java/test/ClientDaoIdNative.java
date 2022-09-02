package test;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import examples.dao.EmployeeDao;
import examples.entity.Employee;

/**
 * 测试ID主键规则： Int 自增
 * （注意：测试该方法需要把数据库ID类型改成int，自增）
 */
public class ClientDaoIdNative {
	public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		EmployeeDao employeeDao = (EmployeeDao) factory.getBean("employeeDao");
		
		Employee employee = new Employee();
		employee.setEmpno("2001");
		employee.setName("scott1");
		employee.setBirthday(new Date());
		employee.setAge(21);
		employee.setSalary(new BigDecimal(88888111));
		employeeDao.insertNative(employee);
		System.out.println("对象值："+employee.toString());
	}
}
