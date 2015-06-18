package examples.dao.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jeecgframework.minidao.annotation.Arguments;
import org.jeecgframework.minidao.annotation.MiniDao;
import org.jeecgframework.minidao.hibernate.MiniDaoSupportHiber;

import examples.entity.user.User;

@MiniDao
public interface UserDao extends MiniDaoSupportHiber<User> {
	
	@Arguments("parm_age")
	public List<Map<String,Object>> listUserByAge(Integer age);

	@Arguments({"name","age","birthday"})
	public void updateUserBirthday(String name,Integer age,Date birthday);
	
	String sle();

	@Arguments({"name","age","birthday"})
	void updateDynamicUserBirthday(String name, Integer age, Date date);
}
