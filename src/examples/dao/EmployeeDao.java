package examples.dao;

import java.util.List;
import java.util.Map;

import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.Sql;

import examples.entity.Employee;


/**
 * 传入参数：支持多种类型 List<T>\Map\Object\基本类型
 * @param po
 */

public interface EmployeeDao {

	@Arguments("employee")
	public List<Map> getAllEmployees(Employee employee);
	
	@Arguments("empno")
    Employee getEmployee(String empno);
    
    @Arguments({"empno","name"})
    Map getMap(String empno,String name);

    @Sql("SELECT count(*) FROM employee")
    Integer getCount();

    @Arguments("employee")
    int update(Employee employee);

    @Arguments("employee")
    void insert(Employee employee);
}
