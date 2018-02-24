package examples.service;

import java.util.List;

import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import examples.dao.EmployeeDao;
import examples.entity.Employee;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void sayHello(){
		employeeDao.getCount();
	}
	
	/**
	 * 执行存储过程
	 * （minidao不支持，直接调用存储过程，采用springjdbc方式进行存储过程调用）
	 */
	public void doProcedure(){
		jdbcTemplate.execute("call sp_insert_table('100001')");
	}
	
	/**
	 * 自定义分页
	 * @param employee
	 * @param page  当前页数
	 * @param rows  每页显示条数
	 * @return
	 */
	public MiniDaoPage<Employee> getPageAll(Employee employee,int page,int pageSize){
		MiniDaoPage<Employee>  employeePageList = new MiniDaoPage<Employee> ();
		//分页显示条数
		employeePageList.setRows(pageSize);
		int count = employeeDao.getCount();
		employeePageList.setTotal(count);
		int startRow = (page -1)*pageSize;
		List<Employee> results = employeeDao.getPageList(employee, startRow, pageSize);
		employeePageList.setResults(results);
		return employeePageList;
	}
}
