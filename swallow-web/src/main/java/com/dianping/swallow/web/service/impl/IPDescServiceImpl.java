package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.IPDescDao;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.IPDescService;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipDescService")
public class IPDescServiceImpl implements IPDescService {

	@Autowired
	private IPDescDao ipDescDao;

	@Override
	public boolean insert(IPDesc ipDesc) {
		return ipDescDao.insert(ipDesc);
	}

	@Override
	public boolean update(IPDesc ipDesc) {
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
	public IPDesc findByIp(String ip) {
		return ipDescDao.findByIp(ip);
	}

	@Override
	public IPDesc findById(String id) {
		return ipDescDao.findById(id);
	}

	@Override
	public List<IPDesc> findAll() {
		return ipDescDao.findAll();
	}

}
