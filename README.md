MiniDao (超轻量级JAVA持久层框架)
=======
当前最新版本： 1.6.2 （发布日期：20170818）


###MiniDao 简介及特征

MiniDao 是一款超级轻量的JAVA持久层框架，基于 SpringJdbc + freemarker 实现，具备Mybatis一样的SQL分离灵活性和标签逻辑。最大优点：可无缝集成Hibernate项目，支持事务统一管理，有效解决Hibernate项目，实现灵活的SQL分离问题。 


具有以下特征:

*  O/R mapping不用设置xml，零配置便于维护
* 不需要了解JDBC的知识
* SQL语句和java代码的分离
* 只需接口定义，无需接口实现
* SQL支持脚本语言（强大脚本语言，freemarker语法）
* 支持与hibernate轻量级无缝集成
* 支持自动事务处理和手动事务处理
* 性能优于Mybatis
* SQL标签采用[Freemarker的基本语法](http://blog.csdn.net/zhangdaiscott/article/details/77505453)



### 接口定义[EmployeeDao.java]  
    @MiniDao
    public interface EmployeeDao {
	
     @Arguments({ "employee"})
	 @Sql("select * from employee")
	 List<Map<String,Object>> getAll(Employee employee);
    
     @Sql("select * from employee where id = :id")
	 Employee get(@Param("id") String id);
    
	 @Sql("select * from employee where empno = :empno and  name = :name")
     Map getMap(@Param("empno")String empno,@Param("name")String name);

     @Sql("SELECT count(*) FROM employee")
     Integer getCount();

     int update(@Param("employee") Employee employee);

     void insert(@Param("employee") Employee employee);
	 
	 @ResultType(Employee.class)
	 public MiniDaoPage<Employee> getAll(@Param("employee") Employee employee,@Param("page")  int page,@Param("rows") int rows);
   }
    
    
    
### SQL文件[EmployeeDao_getAllEmployees.sql]
    SELECT * FROM employee where 1=1 
    <#if employee.age ?exists>
	and age = :employee.age
    </#if>
    <#if employee.name ?exists>
	and name = :employee.name
    </#if>
    <#if employee.empno ?exists>
	and empno = :employee.empno
    </#if>

###接口和SQL文件对应目录

![github](http://www.jeecg.org/data/attachment/forum/201308/18/224051ey14ehqe000iegja.jpg "minidao")

	
### MiniDao在spring中配置
    <!-- MiniDao动态代理类 -->
	<bean id="miniDaoHandler" class="org.jeecgframework.minidao.factory.MiniDaoBeanScannerConfigurer">
		<!-- 是使用什么字母做关键字Map的关键字 默认值origin 即和sql保持一致,lower小写(推荐),upper 大写 -->
		<property name="keyType" value="lower"></property>
		<!-- 格式化sql -->
		<property name="formatSql" value="false"></property>
		<!-- 输出sql -->
		<property name="showSql" value="false"></property>
		<!-- 数据库类型 -->
		<property name="dbType" value="mysql"></property>
		<!-- dao地址,配置符合spring方式 -->
		<property name="basePackage" value="examples.dao.*"></property>
		<!-- 使用的注解,默认是Minidao,推荐 Repository-->
		<property name="annotation" value="org.springframework.stereotype.Repository"></property>
		<!-- Minidao拦截器配置 	-->
		<property name="emptyInterceptor" ref="minidaoInterceptor"></property>
	</bean>

### 测试代码
    public class Client {
    public static void main(String args[]) {
		BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
     		
		EmployeeDao employeeDao = (EmployeeDao) factory.getBean("employeeDao");
		Employee employee = new Employee();
		String id = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
		employee.setId(id);
		employee.setEmpno("A001");
		employee.setSalary(new BigDecimal(5000));
		employee.setBirthday(new Date());
		employee.setName("scott");
		employee.setAge(25);
		//调用minidao方法插入
		employeeDao.insert(employee);
	}
    }


技术交流
-----------------------------------
* 作 者：  张代浩
* 论 坛： [www.jeecg.org](http://www.jeecg.org)
* 邮 箱：  jeecg@sina.com
* QQ交流群：325978980、143858350
