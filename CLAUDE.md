# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MiniDao is a lightweight Java persistence framework based on SpringJdbc + Freemarker. It provides MyBatis-like SQL separation and dynamic SQL via Freemarker templates, with JDK dynamic proxies generating DAO implementations from annotated interfaces. Current version: 1.10.22, targeting Java 17 and Spring Boot 3.5.5.

## Build Commands

```bash
mvn clean compile                    # Compile all modules
mvn test                             # Run all tests (note: surefire configured with failOnFail=false)
mvn test -pl minidao-pe              # Run core module tests only
mvn test -pl minidao-pe -Dtest=RemoveOrderByTest  # Run a single test class
mvn clean package                    # Build JARs
mvn clean deploy -P jeecg            # Deploy to JEECG internal repo
mvn clean deploy -P release          # Deploy to Maven Central
```

Tests in `minidao-pe-example` require a database connection configured in `minidao-pe-example/src/main/resources/dbconfig.properties`.

## Module Structure

- **minidao-pe** — Core engine. All annotations, AOP proxy handler, SQL parsing, Freemarker processing, pagination dialects, and utilities. Artifact: `minidao-pe-jsqlparser-4.9`.
- **minidao-spring-boot-starter** — Spring Boot 3 auto-configuration. Registers the bean scanner via `MinidaoAutoConfiguration`. Artifact: `minidao-spring-boot-starter-jsqlparser-4.9`.
- **minidao-pe-example** — Example app and integration tests (Employee CRUD). Also contains SQL parsing and pagination unit tests.

## Architecture (Request Flow)

```
@MiniDao interface method call
  → JDK Dynamic Proxy (MiniDaoHandler)
    → Load SQL from .sql file or @Sql annotation
    → FreemarkerParseFactory processes template (conditionals, parameter substitution)
    → SqlProcessor parses SQL (JsqlparserSqlProcessor49 or SimpleSqlProcessor fallback)
    → EmptyInterceptor hook (optional, e.g. SaaS tenant injection)
    → JdbcTemplate / NamedParameterJdbcTemplate executes query
    → Result mapping (BeanPropertyRowMapper / ColumnMapRowMapper)
    → Returns MiniDaoPage<T>, List<T>, Map, or scalar
```

## Key Source Packages (under `org.jeecgframework.minidao`)

| Package | Purpose |
|---------|---------|
| `annotation` | `@MiniDao`, `@Sql`, `@Param`, `@ResultType`, `@Arguments`, `@IgnoreSaas`, `@TableId` |
| `aop` | `MiniDaoHandler` — the core InvocationHandler for dynamic proxies |
| `factory` | `MiniDaoBeanScannerConfigurer` — classpath scanning and bean registration |
| `sqlparser.impl` | `JsqlparserSqlProcessor49` (JSqlParser 4.9) and `SimpleSqlProcessor` (fallback) |
| `sqlparser.impl.util.v49` | COUNT query generation, ORDER BY removal, SQL Server pagination, subquery handling |
| `pagehelper.dialect` | 28+ database pagination dialects (MySQL, Oracle, PostgreSQL, SQL Server, DM, KingBase, etc.) |
| `util` | `FreemarkerParseFactory`, `MiniDaoUtil`, `SqlInjectionUtil`, `SnowflakeIdWorker` |
| `aspect` | `EmptyInterceptor` — extensible hook for SaaS isolation and dynamic WHERE injection |
| `datasource` | `DynamicDataSource` — thread-local multi-datasource routing |

## SQL File Conventions

- SQL files are stored alongside the DAO interface: `resources/<package-path>/InterfaceName_methodName.sql`
- Example: `resources/examples/sql/EmployeeDao_getAll.sql`
- Syntax: Standard SQL + Freemarker directives (`<#if>`, `<#list>`) with named parameters (`:paramName`)
- IN clauses use helper: `${DaoFormat.getInStrs(array)}`

## SQL Parsing Notes

- The project uses JSqlParser 4.9 for advanced SQL operations (pagination COUNT generation, ORDER BY removal)
- JSqlParser is sensitive to SQL formatting — multiple consecutive newlines cause parse failures. SQL is normalized before parsing (see `bc82f50`).
- When JSqlParser fails, the framework falls back to `SimpleSqlProcessor`
- `MiniDaoUtil.getSqlProcessor()` selects the processor based on classpath availability

## Branch Strategy

- **master** — main branch
- **springboot3-jsqlparser4.9** — active development branch (Spring Boot 3 + JSqlParser 4.9)
- **master-jsqlparser4.9** — Spring Boot 2 variant
