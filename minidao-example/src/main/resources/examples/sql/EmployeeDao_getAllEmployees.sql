select * from (SELECT * FROM employee where
<#include "EmployeeDao_getAllEmployees_condition.sql">
) a where
<#if employee.age ?exists>
	and a.age = :employee.age
</#if>
<#if employee.name ?exists>
	and a.name = :employee.name
</#if>
<#if employee.empno ?exists>
	and a.empno = :employee.empno
</#if>