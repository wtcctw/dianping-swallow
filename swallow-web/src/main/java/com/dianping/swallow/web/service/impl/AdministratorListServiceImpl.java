package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AdministratorListService;

/**
 * @author mingdongli
 *
 * 2015年5月21日下午8:36:06
 */
@Service("administratorListService")
public class AdministratorListServiceImpl extends AbstractSwallowService implements AdministratorListService {

	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

	@Autowired
	private AdministratorDao administratorDao;
	
	@Override
	public void updateAdmin(Administrator admin, String name, int auth) {
		
		admin.setName(name);
		admin.setRole(auth);
		admin.setDate(new SimpleDateFormat(TIMEFORMAT).format(new Date()));
		administratorDao.saveAdministrator(admin);
		if(logger.isInfoEnabled()){
			logger.info("update admin to : " + admin);
		}

	}

	@Override
	public void doneCreateAdmin(String name, int auth) {

		Administrator admin = getAdministrator(name, auth);
		administratorDao.createAdministrator(admin);
		if(logger.isInfoEnabled()){
			logger.info("create admin: " + admin);
		}
	}
	
	private Administrator getAdministrator(String name, int role) {
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Administrator admin = new Administrator(name, role, date);
		return admin;
	}

}
