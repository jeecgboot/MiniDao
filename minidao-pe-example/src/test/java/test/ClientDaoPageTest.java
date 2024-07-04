package test;

import examples.dao.EmployeeDao;
import examples.entity.Employee;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试ID主键规则： Int 自增
 * （注意：测试该方法需要把数据库ID类型改成int，自增）
 */
public class ClientDaoPageTest {
	private final static Log log = LogFactory.getLog(ClientDaoPageTest.class);
	public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		EmployeeDao employeeDao = (EmployeeDao) factory.getBean("employeeDao");

		//第一页数据
		MiniDaoPage<Employee> pageList1 = employeeDao.getAll(new Employee(),2,10);
		log.info("getTotal1 = "+pageList1.getTotal());
		log.info("getPages1 = "+pageList1.getPages());


		//第二页数据
		MiniDaoPage<Employee> pageList2 = employeeDao.getAll(new Employee(),2,10);
		log.info("getTotal2 = "+pageList2.getTotal());
		log.info("getPages2 = "+pageList2.getPages());
	}
}
