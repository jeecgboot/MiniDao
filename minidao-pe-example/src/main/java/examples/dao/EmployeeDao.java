package examples.dao;

import java.util.List;
import java.util.Map;

import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.pojo.MiniDaoPage;
import org.springframework.stereotype.Repository;

import examples.entity.Employee;

/**
 * 描述：雇员
 * @author：www.jeecg.org
 * @since：2017年08月18日 14时31分08秒 星期五 
 * @version:1.0
 */
@Repository
public interface EmployeeDao {

	/**
	 * 查询返回Java对象
	 * @param id
	 * @return
	 */
	@Sql("select * from employee where id = :id")
	Employee get(@Param("id") String id);
	
	/**
	 * 修改数据
	 * @param employee
	 * @return
	 */
	int update(@Param("employee") Employee employee);
	
	/**
	 * 插入数据
	 * @param employee
	 */
	void insert(@Param("employee") Employee employee);
	
	/**
	 * 通用分页方法，支持（oracle、mysql、SqlServer、postgresql）
	 * @param employee
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResultType(Employee.class)
	public MiniDaoPage<Employee> getAll(@Param("employee") Employee employee,@Param("page")  int page,@Param("rows") int rows);
	
	/**
	 * 删除数据
	 * @param employee
	 */
	@Sql("delete from employee where id = :id")
	public void delete(@Param("id") String id);
	
	/**
	 * 返回List<Map>类型，全部数据
	 * @param employee
	 * @return
	 */
	@Arguments({ "employee"})
	@Sql("select * from employee")
	List<Map<String,Object>> getAll(Employee employee);
	
	/**
	 * 支持多个参数，查看返回Map
	 * @param empno
	 * @param name
	 * @return
	 */
	@Sql("select * from employee where empno = :empno and  name = :name")
	Map<String,Object> getMap(@Param("empno") String empno,@Param("name")String name);
	
	/**
	 * 查询分页
	 * @return
	 */
	@Sql("select count(*) from employee")
	Integer getCount();
}
