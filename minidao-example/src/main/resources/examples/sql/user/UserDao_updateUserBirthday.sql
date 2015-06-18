update user set birthday = '${birthday?string("yyyy-MM-dd")}' where
<#if name ?exists>
	and name = '${name}'
</#if>
<#if age ?exists>
	and age = ${age}
</#if>

