package test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import examples.service.EmployeeService;

/**
 * 测试事务一致性（采用声明注解）
 * @author qinfeng
 *
 */
public class ClientTransactionalDao {
	public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		EmployeeService employeeService = (EmployeeService) factory.getBean("employeeService");
		employeeService.testTransactionalInsert();
	}
}
