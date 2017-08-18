UPDATE employee
SET 
	   <#if employee.empno ?exists>
		   empno = :employee.empno,
		</#if>
	   <#if employee.name ?exists>
		   NAME = :employee.name,
		</#if>
	   <#if employee.age ?exists>
		   AGE = :employee.age,
		</#if>
	    <#if employee.birthday ?exists>
		   BIRTHDAY = :employee.birthday,
		</#if>
	   <#if employee.salary ?exists>
		   SALARY = :employee.salary,
		</#if>
	   <#if employee.createBy ?exists>
		   create_by = :employee.createBy,
		</#if>
	    <#if employee.createDate ?exists>
		   create_date = :employee.createDate,
		</#if>
	   <#if employee.updateBy ?exists>
		   update_by = :employee.updateBy,
		</#if>
	    <#if employee.updateDate ?exists>
		   update_date = :employee.updateDate,
		</#if>
WHERE id = :employee.id