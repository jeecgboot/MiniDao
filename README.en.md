English | [中文](./README.md) 

MiniDao 
=======
Current Version: 1.10.21 (Release Date: 2026-04-14)

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-Beijing%20Guoju%20Software-orange.svg)](http://jeecg.com/aboutusIndex)
[![](https://img.shields.io/badge/Blog-Official%20Blog-blue.svg)](https://jeecg.blog.csdn.net)
[![](https://img.shields.io/badge/version-1.10.21-brightgreen.svg)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub stars](https://img.shields.io/github/stars/jeecgboot/MiniDao.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/jeecgboot/MiniDao.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



 > Feedback: Please [create an issue](https://github.com/jeecgboot/MiniDao/issues/new) on GitHub if you find any bugs

### MiniDao Introduction and Features

A powerful enhanced toolkit of SpringJdbc for simplified development.

MiniDao is a lightweight JAVA persistence layer framework based on SpringJdbc + Freemarker. It has the same SQL separation and logical tag capabilities as MyBatis. The original intention of MiniDao is to provide Hibernate projects with the same flexible capabilities as MyBatis for complex SQL, while supporting transaction synchronization.


Key Features:

* O/R mapping without XML configuration, zero configuration for easy maintenance
* No need to understand JDBC
* Separation of SQL statements and Java code
* Only interface definition required, no implementation needed
* SQL supports scripting language (powerful Freemarker syntax)
* Seamless lightweight integration with Hibernate
* Supports both automatic and manual transaction handling
* Better performance than MyBatis
* Simpler and easier to use than MyBatis
* SQL supports annotation mode
* SQL supports separate file mode. Naming rule: `ClassName_MethodName`; SQL files are easier to locate, convenient for later maintenance
* SQL tags use [Freemarker basic syntax](http://blog.csdn.net/zhangdaiscott/article/details/77505453)



How to Quickly Integrate MiniDao?
-----------------------------------
**Spring Boot 3** is the recommended default (maps to the `main` branch, actively maintained):

```
<dependency>
    <groupId>org.jeecgframework.boot3</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.21</version>
</dependency>
```

If your project is still on Spring Boot 2, use the `springboot2-jsqlparser4.9` maintenance branch:

```
<dependency>
    <groupId>org.jeecgframework</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.15</version>
</dependency>
```

- [MiniDao Official Documentation](https://help.jeecg.com/minidao)




Technical Communication
-----------------------------------
* Documentation: [https://help.jeecg.com/minidao](https://help.jeecg.com/minidao)
* JEECG Low-Code: [www.jeecg.com](http://www.jeecg.com)
* QiaoQiaoYun No-Code: [www.qiaoqiaoyun.com](https://www.qiaoqiaoyun.com)

Project Structure
-----------------------------------


| Module | Description | Remarks |
| --- | --- | --- |
| minidao-pe | Core engine |  |
| minidao-spring-boot-starter | Spring Boot starter auto-configuration |  |
| minidao-pe-example | Sample code and integration tests |  |



Branch Description
-----------------------------------
> Note: The default branch is now **main** (Spring Boot 3 line). Spring Boot 2 branches are in maintenance-only mode. The jsqlparser 4.6 branch will no longer receive new features.

| Branch | Spring Boot | Java | JSqlParser | GroupId | Version | Status |
| --- | --- | --- | --- | --- | --- | --- |
| main | 3.5.5 | 17 | 5.0 | `org.jeecgframework.boot3` | 1.10.21 | Default / Active |
| springboot2-jsqlparser4.9 | 2.x | 8 | 4.9 | `org.jeecgframework` | 1.10.15 | Maintenance |
| springboot2-jsqlparser4.6 | 2.x | 8 | 4.6 | `org.jeecgframework` | 1.10.11 | Deprecated |

### Per-Branch Technical Architecture

**main** (Spring Boot 3 line — default)
- Runtime: Spring Boot 3.5.5, Spring Framework 6.x, Jakarta EE 9+ (`jakarta.*` namespace), Java 17
- Persistence: Spring JDBC (`JdbcTemplate` / `NamedParameterJdbcTemplate`), no ORM required
- Template engine: Freemarker for dynamic SQL composition (`<#if>`, `<#list>`, named params `:name`)
- SQL parser: JSqlParser 5.0 via `JsqlparserSqlProcessor49` (COUNT derivation, ORDER BY removal, UNION / sub-query rewriting), with `SimpleSqlProcessor` fallback
- Auto-config: `minidao-spring-boot-starter` registers `MiniDaoBeanScannerConfigurer` through Spring Boot 3 `AutoConfiguration.imports`
- Coordinates: `org.jeecgframework.boot3:minidao-spring-boot-starter-jsqlparser-4.9:1.10.21`

**springboot2-jsqlparser4.9** (Spring Boot 2 + modern parser)
- Runtime: Spring Boot 2.7.x, Spring Framework 5.x, `javax.*` namespace, Java 8
- Same persistence / Freemarker / dialect stack as `main`
- SQL parser: JSqlParser 4.9 (same advanced rewriting capabilities as `main`)
- Auto-config: Spring Boot 2 starter via `spring.factories`
- Coordinates: `org.jeecgframework:minidao-spring-boot-starter-jsqlparser-4.9:1.10.15`
- Use when: you must stay on Spring Boot 2 but want the JSqlParser 4.9 fixes (UNION / sub-query / ORDER BY removal, etc.)

**springboot2-jsqlparser4.6** (legacy)
- Runtime: Spring Boot 2.7.x, Java 8
- SQL parser: JSqlParser 4.6 (older parser — lacks fixes delivered in 4.9)
- Coordinates: `org.jeecgframework:minidao-spring-boot-starter:1.10.11`
- Status: deprecated. Only critical fixes. New projects should not use this branch.

All branches share the same module layout (`minidao-pe` core engine, `minidao-spring-boot-starter` auto-config, `minidao-pe-example` sample app) and the same 28-database pagination dialect set under `org.jeecgframework.minidao.pagehelper.dialect`.



Supports 28 Databases
-----------------------------------

| Database | Support |
| --- | --- |
| MySQL | √ |
| Oracle, Oracle9i | √ |
| SqlServer, SqlServer2012 | √ |
| PostgreSQL | √ |
| DB2, Informix | √ |
| MariaDB | √ |
| SQLite, Hsqldb, Derby, H2 | √ |
| DM (达梦), KingBase (人大金仓), ShenTong (神通) | √ |
| GaussDB (华为高斯), XuGu (虚谷), HighGo (瀚高) | √ |
| Alibaba Cloud PolarDB, PPAS, HerdDB | √ |
| Hive, HBase, CouchBase | √ |



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
		// Call minidao method to insert
		employeeDao.insert(employee);
	}
    }
