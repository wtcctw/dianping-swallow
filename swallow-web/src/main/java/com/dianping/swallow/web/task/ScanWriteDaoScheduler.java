package com.dianping.swallow.web.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.dianping.swallow.web.controller.TopicController;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.dao.WebSwallowMessageDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AccessControlServiceConstants;
import com.dianping.swallow.web.service.AdministratorService;
import com.dianping.swallow.web.service.impl.AccessControlServiceImpl;
import com.mongodb.Mongo;

/**
 * @author mingdongli
 * 2015年5月12日 下午2:52:05
 */
public class ScanWriteDaoScheduler {
	
	private static final String 				TOPIC_DB_NAME 				= "swallowwebapplication";
	private static final String 				TIMEFORMAT 					= "yyyy-MM-dd HH:mm";

	private static final String 				PRE_MSG 					= "msg#";
	private static final String 				DELIMITOR 					= ",";
	
	@Resource(name = "accessControlService")
	private AccessControlServiceImpl 			accessControlService;
    @Resource(name = "administratorService")
    private AdministratorService 				administratorService;
	@Resource(name = "topicMongoTemplate")
	private MongoTemplate 						mongoTemplate;
	@Autowired
	private WebSwallowMessageDao 				webSwallowMessageDao;
	@Autowired
	private TopicDao 							topicDao;
	@Autowired
	private AdministratorDao 					administratorDao;
	

	private static final Logger 				logger = 					
			LoggerFactory.getLogger(TopicController.class);
	
	@Scheduled(fixedDelay = 10000)
	public void scanTopicDatabase() {
		if (logger.isInfoEnabled()) {
			logger.info("[scanTopicDatabase]");
		}
		List<String> dbs = getDatabaseName();
		boolean isTopicDbexist = isDatabaseExist();

		if (isTopicDbexist)
			updateTopicDb(dbs);
		else
			createTopicDb(dbs);
		
		// scan admin collection to add
		scanAdminCollection();
	}
	
	private void createTopicDb(List<String> dbs){
		for (String dbn : dbs) {
			Set<String> names = new HashSet<String>();
			if (isTopicName(dbn)) {
				String subStr = dbn.substring(PRE_MSG.length()).trim();
				if (!names.contains(subStr)) { // in case add twice
					names.add(subStr);
					saveTopic(subStr);
				}
			}
		}
	}
	
	private void updateTopicDb(List<String> dbs){
		for (String str : dbs) {
			if (isTopicName(str)) {
				String subStr = str.substring(PRE_MSG.length());
				Topic t = topicDao.readByName(subStr);
				if (t != null) { // exists
					updateTopicToWhiteList(subStr, t);
				} else {
					saveTopic(subStr);
				}
			}
		}
	}
	
	private void saveTopic(String subStr){
		topicDao.saveTopic(getTopic(subStr, 0L));
		if (logger.isInfoEnabled()) {
			logger.info("create topic : " + getTopic(subStr, 0L));
		}
	}
	
	private void updateTopicToWhiteList(String subStr, Topic t){
		if (accessControlService.getTopicToWhiteList().get(
				subStr) == null) { // first scan
			Set<String> lists = splitProps(t.getProp());
			accessControlService.getTopicToWhiteList().put(
					subStr, lists);
			if (logger.isInfoEnabled()) {
				logger.info("add " + subStr + " 's whitelist "
						+ lists);
			}
		}
	}
	
	private boolean isDatabaseExist(){
		List<String> writeDbNames = mongoTemplate.getDb().getMongo()
				.getDatabaseNames();
		if (writeDbNames.contains(TOPIC_DB_NAME)) {
			return true;
		}
		else
			return false;
	}

	private List<String> getDatabaseName(){
		List<String> dbs = new ArrayList<String>();
		List<Mongo> allReadMongo = webSwallowMessageDao.getAllReadMongo();
		for (Mongo mc : allReadMongo) {
			dbs.addAll(mc.getDatabaseNames());
		}
		Collections.sort(dbs);
		return dbs;
	}
	
	private void scanAdminCollection(){
		List<Administrator> aList = administratorDao.findAll();
		int role = -1;
		for (Administrator list : aList) {
			role = list.getRole();
			if ( role == AccessControlServiceConstants.ADMINI){
				if (accessControlService.getAdminSet().add(
						list.getName()))
					if (logger.isInfoEnabled()) {
						logger.info("admiSet add " + list.getName());
					}
			}
		}
		if(accessControlService.getAdminSet().isEmpty()){
			String defaultAdmin = accessControlService.getDefaultAdmin();
			administratorService.createInAdminList(defaultAdmin, AccessControlServiceConstants.ADMINI);
			accessControlService.getAdminSet().add(accessControlService.getDefaultAdmin()); //add default admin
			if (logger.isInfoEnabled()) {
				logger.info("admiSet add default admin.");
			}
		}

	}
	
	private boolean isTopicName(String str) {
		if (str != null && str.startsWith(PRE_MSG))
			return true;
		else
			return false;
	}

	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

	private Topic getTopic(String subStr, long num) {
		Long id = System.currentTimeMillis();
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Topic p = new Topic(id.toString(), subStr, "", "", date, num);
		return p;
	}
}
