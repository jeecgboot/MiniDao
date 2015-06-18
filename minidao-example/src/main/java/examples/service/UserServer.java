package examples.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import examples.dao.user.UserDao;
import examples.entity.user.User;

@Service
public class UserServer {
	
	@Autowired
	private UserDao userDao;

	public void saveByHiber(User user) {
		userDao.saveByHiber(user);
	}

	public void updateByHiber(User user) {
		userDao.updateByHiber(user);
	}

	public User getByIdHiber(Class<User> class1, String id) {
		return userDao.getByIdHiber(User.class, id);
	}

	public void deleteByIdHiber(Class<User> class1, String id) {
		userDao.deleteByIdHiber(class1, id);
	}

	public List<User> listByHiber(User user) {
		return userDao.listByHiber(user);
	}

	public List<Map<String, Object>> listUserByAge(int i) {
		return userDao.listUserByAge(i);
	}

	public void updateUserBirthday(String string, Integer age, Date date) {
		userDao.updateUserBirthday(string, age, date);
	}

	/**
	 * 动态更新
	 * @param name
	 * @param age
	 * @param date
	 */
	public void updateDynamicUserBirthday(String name, Integer age, Date date) {
		userDao.updateDynamicUserBirthday(name,age,date);
	}
}
