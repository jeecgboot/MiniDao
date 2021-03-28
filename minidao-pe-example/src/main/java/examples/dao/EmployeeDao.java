package examples.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.IdAutoGenerator;
import org.jeecgframework.minidao.annotation.Param;
import org.jeecgframework.minidao.annotation.ResultType;
import org.jeecgframework.minidao.annotation.Sql;
import org.jeecgframework.minidao.annotation.type.IdType;
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
	 * 查询返回Java对象
	 * @param ids
	 * @return
	 */
	@Sql("select * from employee where id in ( ${DaoFormat.getInStrs(ids)} )")
	List<Map<String,Object>> getEmployeeByIds(@Param("ids") String[] ids);
	
	/**
	 * 查询返回Java对象
	 * @deprecated SQL中采用freemarker语法取值,注意需要手工加上单引号（这种写法有SQL注入风险）
	 * @param id
	 * @return
	 */
	@Sql("select * from employee where id = '${id}'")
	Employee getF(@Param("id") String id);
	
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
	 * 插入数据（ID采用自增策略，并返回自增ID）
	 * @param employee
	 */
	@IdAutoGenerator(type=IdType.AUTO)
	int insertNative(@Param("employee") Employee employee);

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
	 * 自定义分页
	 * @param employee
	 * @param startRow  开始序号
	 * @param pageSize  每页显示条数
	 * @return
	 */
	@ResultType(Employee.class)
	@Sql("select * from employee order by id asc limit :startRow,:pageSize")
	public List<Employee> getPageList(@Param("employee") Employee employee,@Param("startRow")  int startRow,@Param("pageSize") int pageSize);
	
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
	 * 返回List<Map>类型，全部数据
	 * @param employee
	 * @return
	 */
	@Sql("select name from employee")
	List<String> getAllStr();
	
	/**
	 * 返回List<Map>类型，全部数据
	 * @param employee
	 * @return
	 */
	@Sql("select birthday from employee")
	List<Date> getAllDateStr();
	
	/**
	 * 返回Map类型，支持多个参数
	 * @param empno
	 * @param name
	 * @return
	 */
	@Sql("select * from employee where empno = :empno and  name = :name")
	Map<String,Object> getMap(@Param("empno") String empno,@Param("name")String name);
	
	/**
	 * 查询分页数量
	 * @return
	 */
	@Sql("select count(*) from employee")
	Integer getCount();
}
