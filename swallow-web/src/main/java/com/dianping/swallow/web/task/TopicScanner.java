package com.dianping.swallow.web.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.web.dao.impl.WebMongoManager;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.model.UserType;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.service.UserService;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * @author mingdongli 2015年5月12日 下午2:52:05
 */
@Component
public class TopicScanner {

	private static final String TOPIC_DB_NAME = "swallowwebapplication";
	
	public static final String TIMEFORMAT = "yyyy-MM-dd HH:mm";

	private static final String DELIMITOR = ",";
	
	@Value("${swallow.web.admin.defaultadmin}")
	private String defaultAdmin;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "topicService")
	private TopicService topicService;

	@Resource(name = "topicMongoTemplate")
	private MongoTemplate mongoTemplate;

	@Autowired
	private WebMongoManager webMongoManager;

	private Map<String, Set<String>> topics = new ConcurrentHashMap<String, Set<String>>();

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Scheduled(fixedDelay = 60000)
	public void scanTopicDatabase() {

		if (logger.isInfoEnabled()) {
			logger.info("[scanTopicDatabase]");
		}
		
		List<String> dbs = getDatabaseName();
		getTopicAndConsumerIds(dbs);
		boolean isTopicDbexist = isDatabaseExist();

		if (!isTopicDbexist){
			createTopicDb(dbs);
		}

		// scan admin collection to add
		scanAdminCollection();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Set<String>> getTopics() {

		return (Map<String, Set<String>>) SerializationUtils.clone((Serializable) topics);
	}

	private void getTopicAndConsumerIds(List<String> dbs) {

		for (String dbName : dbs) {

			if (StringUtils.isEmpty(dbName)) {
				continue;
			}

			dbName = dbName.trim();
			if (isConsumerId(dbName)) {

				String[] split = dbName.split("#");
				if (split.length != 3) {
					logger.warn("[getTopicAndConsumerIds][wrong ackdbname]" + dbName);
					logger.error("wrong db name", new IllegalArgumentException(dbName));
					continue;
				}
				String topic = split[1];
				String consumerId = split[2];
				Set<String> consumerIds = topics.get(topic);
				if (consumerIds == null) {
					consumerIds = new HashSet<String>();
					topics.put(topic, consumerIds);
				}
				consumerIds.add(consumerId);
			}
		}

	}

	private boolean isConsumerId(String dbName) {

		if (dbName.startsWith(DefaultMongoManager.ACK_PREFIX)) {
			return true;
		}
		return false;
	}

	private void createTopicDb(List<String> dbs) {
		for (String dbn : dbs) {
			Set<String> names = new HashSet<String>();
			if (isTopicName(dbn)) {
				String subStr = dbn.substring(DefaultMongoManager.MSG_PREFIX.length()).trim();
				if (!names.contains(subStr)) { // in case add twice
					names.add(subStr);
					topicService.saveTopic(getTopic(subStr, 0L));
				}
			}
		}
	}

	private boolean isDatabaseExist() {
		List<String> writeDbNames = mongoTemplate.getDb().getMongo().getDatabaseNames();
		if (writeDbNames.contains(TOPIC_DB_NAME)) {
			return true;
		} else
			return false;
	}

	private List<String> getDatabaseName() {
		Set<String> dbs = new HashSet<String>();
		Collection<MongoClient> allReadMongo = webMongoManager.getAllReadMongo();
		for (Mongo mc : allReadMongo) {
			dbs.addAll(mc.getDatabaseNames());
		}
		List<String> result = new ArrayList<String>(dbs);
		Collections.sort(result);

		return result;
	}

	private void scanAdminCollection() {
		List<Administrator> aList = userService.loadUsers();
		if (!aList.isEmpty()) {
			for (Administrator list : aList) {
				UserType role = list.getRole();
				String name = list.getName();
				if (role.equals(UserType.ADMINISTRATOR)) {
					if (userService.loadCachedAdministratorSet().add(name)) {
						logger.info("admiSet add " + name);
					}
				}
			}
		} else {
			String[] admins = loadDefaultAdminFromConf();
			for (String admin : admins) {
				userService.loadCachedAdministratorSet().add(admin);
				userService.createUser(admin, UserType.ADMINISTRATOR);
				logger.info("admiSet add admin " + admin);
			}
		}
	}

	private String[] loadDefaultAdminFromConf() {
		
		String[] admins = defaultAdmin.split(DELIMITOR);
		return admins;
	}

	private boolean isTopicName(String str) {

		if (str.startsWith(DefaultMongoManager.MSG_PREFIX)) {
			return true;
		}

		return false;
	}

	private Topic getTopic(String subStr, long num) {
		Long id = System.currentTimeMillis();
		Topic p = new Topic();
		p.setId(id.toString()).setName(subStr).setProp("").setTime(new Date()).setMessageNum(num);
		return p;
	}
	
}