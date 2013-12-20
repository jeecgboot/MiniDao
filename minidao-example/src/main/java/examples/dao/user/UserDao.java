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
	
	@SuppressWarnings("unchecked")
	@Arguments("parm_age")
	public List<Map> listUserByAge(Integer age);

	@Arguments({"name","age","birthday"})
	public void updateUserBirthday(String name,Integer age,Date birthday);
	
	String sle();
}
