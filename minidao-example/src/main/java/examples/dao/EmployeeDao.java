package examples.dao;

import java.util.Map;

import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.pojo.MiniDaoPage;

import examples.entity.Employee;

@MiniDao("employeeDao")
public interface EmployeeDao {

	@Arguments({ "employee", "page", "rows" })
	@ResultType(Employee.class)
	public MiniDaoPage<Employee> getAllEmployees(Employee employee, int page,
			int rows);

	@Arguments("empno")
	Employee getEmployee(String empno);

	@Arguments({ "empno", "name" })
	Map<String,Object> getMap(String empno, String name);

	@Sql("SELECT count(*) FROM employee")
	Integer getCount();

	@Arguments("employee")
	int update(Employee employee);

	@Arguments("employee")
	void insert(Employee employee);
}
