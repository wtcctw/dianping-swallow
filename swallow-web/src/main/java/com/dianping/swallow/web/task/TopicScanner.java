package com.dianping.swallow.web.task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.web.dao.impl.WebMongoManager;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AdministratorService;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.TopicService;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * @author mingdongli 2015年5月12日 下午2:52:05
 */
@Component
public class TopicScanner {

	private static final String TOPIC_DB_NAME = "swallowwebapplication";
	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm";

	private static final String DELIMITOR = ",";

	@Resource(name = "administratorService")
	private AdministratorService administratorService;

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

		if (isTopicDbexist)
			updateTopicDb(dbs);
		else
			createTopicDb(dbs);

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
					Cat.logError("wrong db name", new IllegalArgumentException(dbName));
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
					saveTopic(subStr);
				}
			}
		}
	}

	private void updateTopicDb(List<String> dbs) {
		for (String str : dbs) {
			if (isTopicName(str)) {
				String subStr = str.substring(DefaultMongoManager.MSG_PREFIX.length());
				Topic t = topicService.loadTopic(subStr);
				if (t != null) { // exists
					updateTopicToWhiteList(subStr, t);
				} else {
					saveTopic(subStr);
				}
			}
		}
	}

	private int saveTopic(String subStr) {
		return topicService.saveTopic(getTopic(subStr, 0L));
	}

	private void updateTopicToWhiteList(String subStr, Topic t) {
		if (topicService.loadTopicToWhiteList().get(subStr) == null) {
			Set<String> lists = splitProps(t.getProp());
			topicService.loadTopicToWhiteList().put(subStr, lists);
			logger.info("add " + subStr + " 's whitelist " + lists);
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
		List<Administrator> aList = administratorService.loadAllAdmin();
		int role = -1;
		if (!aList.isEmpty()) {
			for (Administrator list : aList) {
				role = list.getRole();
				String name = list.getName();
				if (role == AuthenticationService.ADMINI) {
					if (administratorService.loadAdminSet().add(name)) {
						logger.info("admiSet add " + name);
					}
				}
			}
		} else {
			String[] admins = loadDefaultAdminFromConf();
			for (String admin : admins) {
				administratorService.loadAdminSet().add(admin);
				administratorService.createAdmin(admin, AuthenticationService.ADMINI);
				logger.info("admiSet add admin " + admin);
			}
		}
	}

	private String[] loadDefaultAdminFromConf() {
		String defaultAdmin = administratorService.loadDefaultAdmin();
		String[] admins = defaultAdmin.split(DELIMITOR);
		return admins;
	}

	private boolean isTopicName(String str) {

		if (str.startsWith(DefaultMongoManager.MSG_PREFIX)) {
			return true;
		}

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
		Topic p = new Topic();
		p.setId(id.toString()).setName(subStr).setProp("").setTime(date).setMessageNum(num);
		return p;
	}
}