<#if ( employee.name )?? && employee.name ?length gt 0>
    /* 姓名 */
    and name = :employee.name
</#if>
<#if ( employee.age )??>
    /* 年龄 */
    and age = :employee.age
</#if>
