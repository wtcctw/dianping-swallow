package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AdministratorService;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.TopicService;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午8:04:43
 */
@Service("administratorService")
public class AdministratorServiceImpl extends AbstractSwallowService implements AdministratorService {

	private static final String ADMIN = "admin";
	private static final String SIZE = "size";
	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

	@Value("${swallow.web.admin.defaultadmin}")
	private String defaultAdmin;

	@Autowired
	private AdministratorDao administratorDao;

	@Resource(name = "topicService")
	private TopicService topicService;

	private Set<String> adminSet = new HashSet<String>();

	@Override
	public Map<String, Object> loadAdmin(int offset, int limit) {

		return getFixedAdministratorFromExisting(offset, limit);
	}

	private Map<String, Object> getFixedAdministratorFromExisting(int start, int span) {
		Long totalNumOfTopic = administratorDao.countAdministrator();
		List<Administrator> administratorList = administratorDao.findFixedAdministrator(start, span);

		return buildResponse(administratorList, totalNumOfTopic);
	}

	private Map<String, Object> buildResponse(List<Administrator> administratorList, Long adminSize) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(SIZE, adminSize);
		map.put(ADMIN, administratorList);
		return map;
	}

	@Override
	public boolean createAdmin(String name, int auth) {

		if (auth == 0) {
			this.loadAdminSet().add(name); // create, need add in adminSet in
											// memory
			logger.info(String.format("Add administrator %s to admin list.", name));
		} else {
			this.loadAdminSet().remove(name); // edit, need remove in adminSet
												// in memory
			logger.info(String.format("Remove administrator %s from admin list.", name));
		}
		return this.updateAdmin(name, auth);
	}

	@Override
	public boolean removeAdmin(String name) {
		this.loadAdminSet().remove(name);
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
	public List<String> loadAllTypeName() {
		List<String> adminLists = new ArrayList<String>();
		List<Administrator> admins = administratorDao.findAll();
		for (int i = 0; i < admins.size(); ++i) {
			adminLists.add(admins.get(i).getName());
		}
		return adminLists;
	}

	@Override
	public boolean updateAdmin(String name, int auth) {
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
	public Set<String> loadAdminSet() {

		return adminSet;
	}

	@Override
	public String loadDefaultAdmin() {
		return defaultAdmin;
	}

	@Override
	public boolean recordVisitToAdmin(String username) {
		Administrator admin = administratorDao.readByName(username);
		if (admin != null) {
			int role = admin.getRole();
			if (role == 0) {
				return updateAdmin(username, role);
			}
		}

		return switchUserAndVisitor(username);
	}

	private boolean switchUserAndVisitor(String username) {
		if (isUser(username)) {
			return this.updateAdmin(username, AuthenticationService.USER);
		} else {
			return this.updateAdmin(username, AuthenticationService.VISITOR);
		}
	}

	private boolean isUser(String username) {

		Collection<Set<String>> topicUsers = topicService.loadTopicToWhiteList().values();
		for (Set<String> set : topicUsers) {
			if (set.contains(username)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Administrator> loadAllAdmin() {
		return administratorDao.findAll();
	}

}
