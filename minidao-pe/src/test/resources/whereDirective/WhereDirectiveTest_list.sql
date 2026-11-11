select * from employee
<@where>
<#include "WhereDirectiveTest_condition.sql">
</@where>
order by id
