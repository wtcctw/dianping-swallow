package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.service.UserService;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午8:04:43
 */
@Service("userService")
public class UserServiceImpl extends AbstractSwallowService implements UserService {

	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

	@Value("${swallow.web.admin.defaultadmin}")
	private String defaultAdmin;

	@Autowired
	private AdministratorDao administratorDao;

	@Resource(name = "topicService")
	private TopicService topicService;

	private Set<String> adminSet = new HashSet<String>();

	@Override
	public Pair<Long, List<Administrator>> loadUserPage(int offset, int limit) {

		Long totalNumOfTopic = administratorDao.countAdministrator();
		List<Administrator> administratorList = administratorDao.findFixedAdministrator(offset, limit);
		
		return new Pair<Long, List<Administrator>>(totalNumOfTopic, administratorList);
	}

	@Override
	public boolean createUser(String name, int auth) {

		if (auth == 0) {
			this.loadCachedAdministratorSet().add(name); // create, need add in adminSet in
											// memory
			logger.info(String.format("Add administrator %s to admin list.", name));
		} else {
			this.loadCachedAdministratorSet().remove(name); // edit, need remove in adminSet
												// in memory
			logger.info(String.format("Remove administrator %s from admin list.", name));
		}
		return this.updateUser(name, auth);
	}

	@Override
	public boolean removeUser(String name) {
		this.loadCachedAdministratorSet().remove(name);
		int n = administratorDao.deleteByName(name);
		if (n != 1) {
			logger.info("deleteByName is wrong with name: " + name);
			return false;
		} else {
			logger.info("delete administrator with name [" + name + "]");
			return true;
		}
	}
	
	@Override
	public List<Administrator> loadUsers() {
		return administratorDao.findAll();
	}

	@Override
	public boolean updateUser(String name, int auth) {
		Administrator admin = administratorDao.readByName(name);
		if (admin == null) {
			return doneCreateAdmin(name, auth);
		} else {
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

	@Override
	public Set<String> loadCachedAdministratorSet() {

		return adminSet;
	}

	@Override
	public boolean createOrUpdateUser(String username) {
		Administrator admin = administratorDao.readByName(username);
		if (admin != null) {
			int role = admin.getRole();
			if (role == 0) {
				return updateUser(username, role);
			}
		}

		return switchTopicOwnerAndVisitor(username);
	}

	private boolean switchTopicOwnerAndVisitor(String username) {
		if (isTopicOwner(username)) {
			return this.updateUser(username, AuthenticationService.USER);
		} else {
			return this.updateUser(username, AuthenticationService.VISITOR);
		}
	}

	private boolean isTopicOwner(String username) {

		Collection<Set<String>> topicUsers = topicService.loadCachedTopicToWhiteList().values();
		for (Set<String> set : topicUsers) {
			if (set.contains(username)) {
				return true;
			}
		}
		return false;
	}

}
