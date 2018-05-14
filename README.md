MiniDao (轻量级JAVA持久层，Hibernate完美助手)
=======
当前最新版本： 1.6.2 （发布日期：20180309）

### MiniDao产生的初衷？

  采用Hibernate的J2EE项目都有一个痛病，针对复杂业务SQL，hibernate能力不足，SQL不好优化和也无法分离。 这个时候大家就想到集成mybatis，但是一个项目既用hibernate又用mybatis，显得很重事务也不好控制。大家常规的做法是采用springjdbc来实现原生SQL编写，但是也同样存在问题，SQL无法分离也没有逻辑标签能力。
  所以为了解决这个痛病，Jeecg针对springjdbc+freemarker做了封装，出了这么一个轻量级持久层，可以让Hiberate拥有mybatis一样SQL灵活能力，同时支持事务统一、SQL标签能力。


### MiniDao 简介及特征

MiniDao 是一款轻量级JAVA持久层框架，基于 SpringJdbc + freemarker 实现，具备Mybatis一样的SQL分离和逻辑标签能力。Minidao产生的初衷是为了解决Hibernate项目，在复杂SQL具备Mybatis一样的灵活能力，同时支持事务同步。 


具有以下特征:

*  O/R mapping不用设置xml，零配置便于维护
* 不需要了解JDBC的知识
* SQL语句和java代码的分离
* 只需接口定义，无需接口实现
* SQL支持脚本语言（强大脚本语言，freemarker语法）
* 支持与hibernate轻量级无缝集成
* 支持自动事务处理和手动事务处理
* 性能优于Mybatis
* 比Mybatis更简单易用
* SQL标签采用[Freemarker的基本语法](http://blog.csdn.net/zhangdaiscott/article/details/77505453)


技术交流
-----------------------------------
* 作 者：  张代浩
* 文 档： [http://minidao.mydoc.io](http://minidao.mydoc.io)
* 论 坛： [www.jeecg.org](http://www.jeecg.org)
* 邮 箱：  jeecg@sina.com
* QQ交流群：① 325978980


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

