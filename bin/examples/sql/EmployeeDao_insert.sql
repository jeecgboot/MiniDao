insert 
into 
employee
      (id,empno,name,age,birthday,salary) 
values('${employee.id}','${employee.empno?default('')}','${employee.name?default('')}',${employee.age?default('null')},${employee.birthday?default('null')},${employee.salary?default('null')})