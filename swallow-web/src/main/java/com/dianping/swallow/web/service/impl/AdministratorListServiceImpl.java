package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AdministratorListService;

/**
 * @author mingdongli
 *
 *         2015年5月21日下午8:36:06
 */
@Service("administratorListService")
public class AdministratorListServiceImpl extends AbstractSwallowService
		implements AdministratorListService {

	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

	@Autowired
	private AdministratorDao administratorDao;
	@Autowired
	private TopicDao topicDao;

	@Override
	public boolean updateAdmin(String name, int auth) {
		Administrator admin = administratorDao.readByName(name);
		if(admin == null){
			return doneCreateAdmin(name, auth);
		}
		else{
			admin.setName(name).setRole(auth).setDate(new SimpleDateFormat(TIMEFORMAT).format(new Date()));
			return administratorDao.saveAdministrator(admin);
		}
	}

	private boolean doneCreateAdmin(String name, int auth) {

		Administrator admin = buildAdministrator(name, auth);
		return administratorDao.createAdministrator(admin);
	}


	private Administrator buildAdministrator(String name, int role) {
		Administrator admin = new Administrator();
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		admin.setName(name).setRole(role).setDate(date);
		return admin;
	}

}
