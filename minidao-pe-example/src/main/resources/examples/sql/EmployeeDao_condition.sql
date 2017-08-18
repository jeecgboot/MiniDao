		<#if ( employee.empno )?? && employee.empno ?length gt 0>
		    /* 雇员编号 */
			and e.empno = :employee.empno
		</#if>
		<#if ( employee.name )?? && employee.name ?length gt 0>
		    /* 雇员名 */
			and e.NAME = :employee.name
		</#if>
		<#if ( employee.age )?? && employee.age ?length gt 0>
		    /* 年龄 */
			and e.AGE = :employee.age
		</#if>
	    <#if ( employee.birthday )??>
		    /* 生日 */
			and e.BIRTHDAY = :employee.birthday
		</#if>
		<#if ( employee.salary )?? && employee.salary ?length gt 0>
		    /* 工资 */
			and e.SALARY = :employee.salary
		</#if>
		<#if ( employee.createBy )?? && employee.createBy ?length gt 0>
		    /* create_by */
			and e.create_by = :employee.createBy
		</#if>
	    <#if ( employee.createDate )??>
		    /* create_date */
			and e.create_date = :employee.createDate
		</#if>
		<#if ( employee.updateBy )?? && employee.updateBy ?length gt 0>
		    /* update_by */
			and e.update_by = :employee.updateBy
		</#if>
	    <#if ( employee.updateDate )??>
		    /* update_date */
			and e.update_date = :employee.updateDate
		</#if>
