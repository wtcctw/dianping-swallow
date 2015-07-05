package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.IpDescDao;
import com.dianping.swallow.web.model.cmdb.IpDesc;
import com.dianping.swallow.web.service.IpDescService;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipDescService")
public class IpDescServiceImpl implements IpDescService {

	@Autowired
	private IpDescDao ipDescDao;

	@Override
	public boolean insert(IpDesc ipDesc) {
		return ipDescDao.insert(ipDesc);
	}

	@Override
	public boolean update(IpDesc ipDesc) {
		return ipDescDao.update(ipDesc);
	}

	@Override
	public int deleteById(String id) {
		return ipDescDao.deleteById(id);
	}

	@Override
	public int deleteByIp(String ip) {
		return ipDescDao.deleteByIp(ip);
	}

	@Override
	public IpDesc findByIp(String ip) {
		return ipDescDao.findByIp(ip);
	}

	@Override
	public IpDesc findById(String id) {
		return ipDescDao.findById(id);
	}

	@Override
	public List<IpDesc> findAll() {
		return ipDescDao.findAll();
	}

}
