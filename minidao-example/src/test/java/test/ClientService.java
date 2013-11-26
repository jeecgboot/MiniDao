package test;

import java.util.Date;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import examples.entity.Employee;
import examples.entity.JeecgDemo;
import examples.service.EmployeeService;
import examples.service.JeecgDemoService;

public class ClientService {
	public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		JeecgDemoService jeecgDemoService = (JeecgDemoService) factory.getBean("jeecgDemoService");
		JeecgDemo jeecgDemo = new JeecgDemo();
		jeecgDemo.setAge(30);
		jeecgDemo.setBirthday(new Date());
		jeecgDemo.setUserName("张代浩111");
		jeecgDemoService.add(jeecgDemo);
		
	}
}
