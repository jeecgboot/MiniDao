[中文](./README.zh-CN.md) | [English](./README.md)

MiniDao 
=======
Current Version: 1.10.19 (Release Date: 2026-01-16)

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-Beijing%20Guoju%20Software-orange.svg)](http://jeecg.com/aboutusIndex)
[![](https://img.shields.io/badge/Blog-Official%20Blog-blue.svg)](https://jeecg.blog.csdn.net)
[![](https://img.shields.io/badge/version-1.10.19-brightgreen.svg)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub stars](https://img.shields.io/github/stars/jeecgboot/MiniDao.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/jeecgboot/MiniDao.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



 > Feedback: Please [create an issue](https://github.com/jeecgboot/MiniDao/issues/new) on GitHub if you find any bugs

### MiniDao Introduction and Features

An powerful enhanced toolkit of SpringJdbc for simplify development

MiniDao is a lightweight JAVA persistence layer framework based on SpringJdbc + Freemarker implementation. It has the same SQL separation and logical tag capabilities as MyBatis. The original intention of MiniDao is to provide Hibernate projects with the same flexible capabilities as MyBatis for complex SQL, while supporting transaction synchronization.


Key Features:

* O/R mapping without XML configuration, zero configuration for easy maintenance
* No need to understand JDBC knowledge
* Separation of SQL statements and Java code
* Only interface definition required, no interface implementation needed
* SQL supports scripting language (powerful scripting with Freemarker syntax)
* Supports seamless lightweight integration with Hibernate
* Supports both automatic and manual transaction processing
* Better performance than MyBatis
* Simpler and easier to use than MyBatis
* SQL supports annotation mode
* SQL supports separate file mode. SQL file naming rule: ClassName_MethodName; SQL files are easier to locate, convenient for later maintenance. The larger the project, the more obvious this advantage is
* SQL tags use [Freemarker's basic syntax](http://blog.csdn.net/zhangdaiscott/article/details/77505453)



How to Quickly Integrate MiniDao?
-----------------------------------
- SpringBoot 2 Integration
```
<dependency>
    <groupId>org.jeecgframework</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.16</version>
</dependency>
```

- SpringBoot 3 Integration
```
<dependency>
    <groupId>org.jeecgframework.boot3</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.19</version>
</dependency>
```

- [SpringBoot 2 with MiniDao Integration](http://minidao.jeecg.com/2392296)
- [SpringMVC with MiniDao Integration](http://minidao.jeecg.com/2392293)


		

Technical Communication
-----------------------------------
* Documentation: [http://minidao.jeecg.com](http://minidao.jeecg.com)
* JEECG Low-Code: [www.jeecg.com](http://www.jeecg.com)
* QiaoQiaoYun No-Code: [www.qiaoqiaoyun.com](https://www.qiaoqiaoyun.com)

Project Structure
-----------------------------------


| Project Name   |         Description         |  Remarks |
|----------|:-------------------:|------:|
| minidao-pe|        Core Architecture Package        |     |
| minidao-spring-boot-starter | SpringBoot2 Starter |  |
| minidao-pe-example |        Sample Code         |     |	 



Branch Description
-----------------------------------
> Note: The default branch has been switched to master-jsqlparser4.9. The jsqlparser4.6 branch will no longer be maintained.

| Branch                        |            Architecture Description             |   Status |
|---------------------------|:---------------------------:|-----:|
| master-jsqlparser4.9      | SpringBoot2 + jsqlparser4.9 | Default Branch |
| springboot3-jsqlparser4.9 | SpringBoot3 + jsqlparser4.9 | Active Maintenance |
| master                    | SpringBoot2 + jsqlparser4.6 | Deprecated |
| springboot3               | SpringBoot3 + jsqlparser4.6 | Deprecated |	 



Supports 28 Databases
-----------------------------------

|  Database   |  Support   |
| --- | --- |
|   MySQL   |  √   |
|  Oracle, Oracle9i   |  √   |
|  SqlServer, SqlServer2012   |  √   |
|   PostgreSQL   |  √   |
|   DB2, Informix   |  √   |
|   MariaDB   |  √   |
|  SQLite, Hsqldb, Derby, H2   |  √   |
|   DM (达梦), KingBase (人大金仓), ShenTong (神通)   |  √   |
|   GaussDB (华为高斯), XuGu (虚谷), HighGo (瀚高)   |  √   |
|   Alibaba Cloud PolarDB, PPAS, HerdDB   |  √   |
|  Hive, HBase, CouchBase   |  √   |



	
Code Examples
-----------------------------------
#### 1. Interface Definition [EmployeeDao.java]  
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
    
    
#### 2. SQL File [EmployeeDao_getAllEmployees.sql]
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

#### 3. Interface and SQL File Directory Structure

![github](http://www.jeecg.org/data/attachment/forum/201308/18/224051ey14ehqe000iegja.jpg "minidao")

	
#### 4. Test Code
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
		//Call minidao method to insert
		employeeDao.insert(employee);
	}
    }

