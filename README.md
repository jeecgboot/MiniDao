中文 | [English](./README.en.md)


MiniDao 
=======
当前最新版本： 1.10.21 （发布日期：2026-04-14）

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-北京国炬软件-orange.svg)](http://jeecg.com/aboutusIndex)
[![](https://img.shields.io/badge/Blog-官方博客-blue.svg)](https://jeecg.blog.csdn.net)
[![](https://img.shields.io/badge/version-1.10.21-brightgreen.svg)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub stars](https://img.shields.io/github/stars/jeecgboot/MiniDao.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/jeecgboot/MiniDao.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



 > 反馈问题：发现bug请在github [发issue](https://github.com/jeecgboot/MiniDao/issues/new)

### MiniDao 简介及特征

一款基于 SpringJdbc 的增强型轻量级持久层框架

MiniDao 是一款轻量级 JAVA 持久层框架，基于 SpringJdbc + Freemarker 实现，具备 Mybatis 一样的 SQL 分离和逻辑标签能力。MiniDao 产生的初衷是为了解决 Hibernate 项目，在复杂 SQL 场景下具备 Mybatis 一样的灵活能力，同时支持事务同步。


具有以下特征:

* O/R mapping 不用设置 xml，零配置便于维护
* 不需要了解 JDBC 的知识
* SQL 语句和 Java 代码的分离
* 只需接口定义，无需接口实现
* SQL 支持脚本语言（强大的 Freemarker 语法）
* 支持与 Hibernate 轻量级无缝集成
* 支持自动事务处理和手动事务处理
* 性能优于 Mybatis
* 比 Mybatis 更简单易用
* SQL 支持注解方式
* SQL 支持独立文件方式，SQL 文件命名规则：类名_方法名；SQL 文件更容易定位，方便后期维护，项目越大此优势越明显
* SQL 标签采用 [Freemarker 的基本语法](http://blog.csdn.net/zhangdaiscott/article/details/77505453)



如何快速集成 MiniDao ?
-----------------------------------
默认推荐 **Spring Boot 3** 集成方式（对应 `main` 分支，持续更新）：

```
<dependency>
    <groupId>org.jeecgframework.boot3</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.21</version>
</dependency>
```

若项目仍停留在 Spring Boot 2，可使用维护分支 `springboot2-jsqlparser4.9`：

```
<dependency>
    <groupId>org.jeecgframework</groupId>
    <artifactId>minidao-spring-boot-starter-jsqlparser-4.9</artifactId>
    <version>1.10.15</version>
</dependency>
```

- [MiniDao 官方文档](https://help.jeecg.com/minidao)




技术交流
-----------------------------------
* 文　档：[https://help.jeecg.com/minidao](https://help.jeecg.com/minidao)
* JEECG 低代码：[www.jeecg.com](http://www.jeecg.com)
* 敲敲云零代码：[www.qiaoqiaoyun.com](https://www.qiaoqiaoyun.com)

项目结构
-----------------------------------


| 项目名 | 说明 | 备注 |
| --- | --- | --- |
| minidao-pe | 架构核心包 |  |
| minidao-spring-boot-starter | Spring Boot Starter 自动装配 |  |
| minidao-pe-example | 示例代码与集成测试 |  |



分支说明
-----------------------------------
> 注意：默认分支已切换到 **main**（Spring Boot 3 线）。Spring Boot 2 分支仅维护模式，jsqlparser 4.6 分支不再接收新特性。

| 分支 | Spring Boot | Java | JSqlParser | GroupId | 版本 | 状态 |
| --- | --- | --- | --- | --- | --- | --- |
| main | 3.5.5 | 17 | 5.0 | `org.jeecgframework.boot3` | 1.10.21 | 默认 / 活跃 |
| springboot2-jsqlparser4.9 | 2.x | 8 | 4.9 | `org.jeecgframework` | 1.10.15 | 维护 |
| springboot2-jsqlparser4.6 | 2.x | 8 | 4.6 | `org.jeecgframework` | 1.10.11 | 停止维护 |

### 各分支技术架构

**main**（Spring Boot 3 线 — 默认分支）
- 运行时：Spring Boot 3.5.5、Spring Framework 6.x、Jakarta EE 9+（`jakarta.*` 命名空间）、Java 17
- 持久层：Spring JDBC（`JdbcTemplate` / `NamedParameterJdbcTemplate`），无需 ORM
- 模板引擎：Freemarker 实现动态 SQL 组装（`<#if>`、`<#list>`、命名参数 `:name`）
- SQL 解析：JSqlParser 5.0，通过 `JsqlparserSqlProcessor49` 实现（COUNT 语句派生、ORDER BY 剥离、UNION / 子查询改写），`SimpleSqlProcessor` 作为降级兜底
- 自动装配：`minidao-spring-boot-starter` 通过 Spring Boot 3 的 `AutoConfiguration.imports` 机制注册 `MiniDaoBeanScannerConfigurer`
- Maven 坐标：`org.jeecgframework.boot3:minidao-spring-boot-starter-jsqlparser-4.9:1.10.21`

**springboot2-jsqlparser4.9**（Spring Boot 2 + 新版解析器）
- 运行时：Spring Boot 2.7.x、Spring Framework 5.x、`javax.*` 命名空间、Java 8
- 持久层 / Freemarker / 分页方言栈与 `main` 一致
- SQL 解析：JSqlParser 4.9，具备与 `main` 相同的高级 SQL 改写能力
- 自动装配：Spring Boot 2 starter，通过 `spring.factories` 加载
- Maven 坐标：`org.jeecgframework:minidao-spring-boot-starter-jsqlparser-4.9:1.10.15`
- 适用场景：项目必须停留在 Spring Boot 2，但希望享受 JSqlParser 4.9 的修复（UNION + 子查询、ORDER BY 剥离、复杂 LEFT JOIN 表名提取等）

**springboot2-jsqlparser4.6**（遗留分支）
- 运行时：Spring Boot 2.7.x、Java 8
- SQL 解析：JSqlParser 4.6（老版本，缺少 4.9 中的一系列修复）
- Maven 坐标：`org.jeecgframework:minidao-spring-boot-starter:1.10.11`
- 状态：停止维护，仅处理严重问题。新项目请勿使用本分支。

所有分支共享相同的模块布局（`minidao-pe` 核心引擎、`minidao-spring-boot-starter` 自动装配、`minidao-pe-example` 示例）以及同一套位于 `org.jeecgframework.minidao.pagehelper.dialect` 下的 28 种数据库分页方言。



支持 28 种数据库
-----------------------------------

| 数据库 | 支持 |
| --- | --- |
| MySQL | √ |
| Oracle、Oracle9i | √ |
| SqlServer、SqlServer2012 | √ |
| PostgreSQL | √ |
| DB2、Informix | √ |
| MariaDB | √ |
| SQLite、Hsqldb、Derby、H2 | √ |
| 达梦、人大金仓、神通 | √ |
| 华为高斯、虚谷、瀚高数据库 | √ |
| 阿里云 PolarDB、PPAS、HerdDB | √ |
| Hive、HBase、CouchBase | √ |



代码体验
-----------------------------------
#### 1. 接口定义 [EmployeeDao.java]
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


#### 2. SQL 文件 [EmployeeDao_getAllEmployees.sql]
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

#### 3. 接口与 SQL 文件目录结构

![github](https://help.jeecg.com/assets/images/screenshot_1693806133613-bb0533008ee25bdb949c15efc251bbd5.png "minidao")


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
		//调用 minidao 方法插入
		employeeDao.insert(employee);
	}
    }
