package examples.service;

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
	 * @param jeecgDemo
	 */
	public void add(JeecgDemo jeecgDemo){
		jeecgDemoDao.saveByHiber(jeecgDemo);
	}

}
