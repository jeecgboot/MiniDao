package examples.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import examples.dao.JeecgDemoDao;
import examples.entity.JeecgDemo;

@Service
public class JeecgDemoService {

	@Autowired
	private JeecgDemoDao jeecgDemoDao;

	/**
	 * 添加数据
	 * 
	 * @param jeecgDemo
	 */
	public void add(JeecgDemo jeecgDemo) {
		jeecgDemoDao.saveByHiber(jeecgDemo);
	}

	public void saveByHiber(JeecgDemo jeecgDemo) {
		jeecgDemoDao.saveByHiber(jeecgDemo);

	}

	public void updateByHiber(JeecgDemo jeecgDemo) {
		jeecgDemoDao.updateByHiber(jeecgDemo);

	}

	public JeecgDemo getByIdHiber(Class<JeecgDemo> class1, String id) {
		return jeecgDemoDao.getByIdHiber(class1, id);
	}

	public void deleteByHiber(JeecgDemo jeecgDemo) {
		jeecgDemoDao.deleteByHiber(jeecgDemo);
	}

	public void deleteByIdHiber(Class<JeecgDemo> class1, String id) {
		jeecgDemoDao.deleteByIdHiber(JeecgDemo.class, id);
	}

	public List<JeecgDemo> listByHiber(JeecgDemo jeecgDemo) {
		return jeecgDemoDao.listByHiber(jeecgDemo);
	}

}
