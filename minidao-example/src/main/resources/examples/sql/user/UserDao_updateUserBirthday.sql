update user set birthday = '${birthday?string("yyyy-MM-dd")}' where  1=1
<#if name ?exists>
	and name = '${name}'
</#if>
<#if age ?exists>
	and age = ${age}
</#if>

