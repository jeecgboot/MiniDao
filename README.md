MiniDao 
=======
当前最新版本： 1.10.7 （发布日期：2025-05-06）

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-北京国炬软件-orange.svg)](http://jeecg.com/aboutusIndex)
[![](https://img.shields.io/badge/Blog-官方博客-blue.svg)](https://jeecg.blog.csdn.net)
[![](https://img.shields.io/badge/version-1.10.7-brightgreen.svg)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub stars](https://img.shields.io/github/stars/zhangdaiscott/jeecg-boot.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/zhangdaiscott/jeecg-boot.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



 > 反馈问题：发现bug请在github [发issue](https://github.com/jeecgboot/MiniDao/issues/new)

### MiniDao 简介及特征

An powerful enhanced toolkit of SpringJdbc for simplify development

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
* SQL 支持注解方式
* SQL 支持独立文件方式，SQL文件的命名规则: 类名_方法名; SQL文件更容易定位，方便后期维护，项目越大此优势越明显
* SQL标签采用[Freemarker的基本语法](http://blog.csdn.net/zhangdaiscott/article/details/77505453)



如何快速集成minidao?
-----------------------------------
> Springboot2与Springboot3区别在于groupId不同，springboot3版本加上了后缀.boot3

```
<dependency>
  <groupId>org.jeecgframework.boot3</groupId>
  <artifactId>minidao-spring-boot-starter</artifactId>
  <version>1.10.7</version>
</dependency>
```

- [springboot2与minidao集成](http://minidao.jeecg.com/2392296)
- [springmvc与Minidao集成](http://minidao.jeecg.com/2392293)


		

技术交流
-----------------------------------
* 文 档： [http://minidao.jeecg.com](http://minidao.jeecg.com)
* JEECG低代码： [www.jeecg.com](http://www.jeecg.com)
* 敲敲云零代码： [www.qiaoqiaoyun.com](https://www.qiaoqiaoyun.com)

项目介绍
-----------------------------------

| 项目名   |         中文名         |  备注 |
|----------|:-------------------:|------:|
| minidao-pe|        架构核心包        |     |
| minidao-spring-boot-starter | SpringBoot3 starter |  |
| minidao-pe-example |        示例代码         |     |	 
	
	
支持28种数据库
-----------------------------------

|  数据库   |  支持   |
| --- | --- |
|   MySQL   |  √   |
|  Oracle、Oracle9i   |  √   |
|  SqlServer、SqlServer2012   |  √   |
|   PostgreSQL   |  √   |
|   DB2、Informix   |  √   |
|   MariaDB   |  √   |
|  SQLite、Hsqldb、Derby、H2   |  √   |
|   达梦、人大金仓、神通   |  √   |
|   华为高斯、虚谷、瀚高数据库   |  √   |
|   阿里云PolarDB、PPAS、HerdDB   |  √   |
|  Hive、HBase、CouchBase   |  √   |



	
代码体验
-----------------------------------
#### 1. 接口定义[EmployeeDao.java]  
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
    
    
#### 2. SQL文件[EmployeeDao_getAllEmployees.sql]
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

#### 3. 接口和SQL文件对应目录

![github](http://www.jeecg.org/data/attachment/forum/201308/18/224051ey14ehqe000iegja.jpg "minidao")

	
#### 4. 测试代码
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

