package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.task.TopicScanner;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;

import org.apache.commons.lang.StringUtils;

import freemarker.template.utility.StringUtil;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:16:09
 */
@Service("topicService")
public class TopicServiceImpl extends AbstractSwallowService implements TopicService {

	private static final String SWALLOW_TOPIC_WHITELIST_KEY = "swallow.topic.whitelist";

	@Autowired
	private TopicDao topicDao;

	@Resource(name = "userService")
	private UserService userService;

	private ConfigCache configCache;

	private Map<String, Set<String>> topicToWhiteList = new ConcurrentHashMap<String, Set<String>>();

	@PostConstruct
	void initLionConfig() {

		try {
			configCache = ConfigCache.getInstance();
			String key = configCache.getProperty(SWALLOW_TOPIC_WHITELIST_KEY);
			cacheTopicToWhiteList(splitString(key, ";"));
			configCache.addChange(this);
			logger.info("Init configCache successfully.");
		} catch (LionException e) {
			logger.error("Erroe when init lion config", e);
		}
	}

	@Override
	public void onChange(String key, String value) {

		if (key != null && key.equals(SWALLOW_TOPIC_WHITELIST_KEY)) {
			if (logger.isInfoEnabled()) {
				logger.info("[onChange][" + SWALLOW_TOPIC_WHITELIST_KEY + "]" + value);
			}

			String[] whitelist = StringUtil.split(value, ';');

			for (String wl : whitelist) {
				if (StringUtils.isNotBlank(wl) && topicDao.readByName(wl) == null) {
					Topic t = getTopic(wl, 0);
					try {
						int status = saveTopic(t);
						if (logger.isInfoEnabled() && status == 0) {
							logger.info(String.format("Save topic %s to database", wl));
						}else if(logger.isErrorEnabled() && status != 0){
							logger.error(String.format("Save topic %s to database fail with status %d", wl, status));
							continue;
						}
						topicToWhiteList.put(wl, new HashSet<String>());
						if (logger.isInfoEnabled()) {
							logger.info(String.format("Add topic %s to whitelist with empty proposal", wl));
						}
					} catch (Exception e) {
						if (logger.isInfoEnabled()) {
							logger.error("Error when save topic to db", e);
						}
					}
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("not match");
			}
		}
	}

	@Override
	public Pair<Long, List<Topic>> loadTopicPage(int start, int span) {

		return topicDao.loadTopicPage(start, span);
	}

	@Override
	public Pair<Long, List<Topic>> loadSpecificTopicPage(int start, int span, String name, String prop) {

		return topicDao.loadSpecificTopicPage(start, span, name, prop);
	}

	@Override
	public List<String> loadTopicNames(String proposal, boolean isAdmin) {

		Map<String, Set<String>> topicToWhiteList = this.loadCachedTopicToWhiteList();
		if (isAdmin) {
			return new ArrayList<String>(topicToWhiteList.keySet());
		} else {
			List<String> topics = new ArrayList<String>();
			for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
				if (entry.getValue().contains(proposal)) {
					String topic = entry.getKey();
					if (!topics.contains(topic)) {
						topics.add(topic);
					}
				}
			}
			return topics;
		}
	}

	@Override
	public int editTopic(String name, String prop, String time) throws MongoSocketException, MongoException {

		Set<String> proposal = splitString(prop, ",");
		topicToWhiteList.put(name, proposal);
		StringBuffer sb = new StringBuffer();
		boolean first = false;
		for (String p : proposal) {
			if (!first) {
				sb.append(p);
				first = true;
			} else {
				sb.append(",").append(p);
			}
		}
		return topicDao.updateTopic(name, sb.toString(), time);
	}

	@Override
	public Pair<List<String>, List<String>> loadTopicProposal(String username, boolean isAdmin) {

		Set<String> proposal = new HashSet<String>();
		Set<String> editProposal = new HashSet<String>();
		List<Topic> topics = topicDao.LoadTopics();

		if (isAdmin) {
			for (Topic topic : topics) {
				proposal.addAll(getPropList(topic));
			}
		} else {
			for (Topic topic : topics) {
				Set<String> tmpprop = getPropList(topic);
				if (tmpprop.contains(username)) {
					proposal.addAll(tmpprop);
				}
			}

		}

		editProposal.addAll(proposal);
		List<Administrator> adminList = userService.loadUsers();
		for (Administrator admin : adminList) {
			editProposal.add(admin.getName());
		}

		return new Pair<List<String>, List<String>>(new ArrayList<String>(proposal),
				new ArrayList<String>(editProposal));
	}

	@Override
	public List<String> loadTopicNames(String username) {

		List<String> topics = new ArrayList<String>();
		for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
			if (entry.getValue().contains(username)) {
				topics.add(entry.getKey());
			}
		}
		return topics;
	}

	@Override
	public Map<String, Set<String>> loadCachedTopicToWhiteList() {
		return topicToWhiteList;
	}

	@Override
	public int saveTopic(Topic topic) {

		if (StringUtils.isNotBlank(topic.getName())) {
			return topicDao.saveTopic(topic);
		}
		return ResponseStatus.TOPICBLANK.getStatus();
	}

	@Override
	public Topic loadTopicByName(String name) {
		return topicDao.readByName(name);
	}

	private Set<String> getPropList(Topic topic) {
		Set<String> props = new HashSet<String>();
		String[] tmpprops = topic.getProp().split(",");

		for (String tmpProp : tmpprops) {
			if (!StringUtils.isEmpty(tmpProp)) {
				props.add(tmpProp);
			}
		}
		return props;
	}

	private Topic getTopic(String subStr, long num) {

		Long id = System.currentTimeMillis();
		String date = new SimpleDateFormat(TopicScanner.TIMEFORMAT).format(new Date());
		Topic p = new Topic();
		p.setId(id.toString()).setName(subStr).setProp("").setTime(date).setMessageNum(num);
		return p;
	}

	private void cacheTopicToWhiteList(Set<String> whiteList) {

		for (String str : whiteList) {
			Topic topic = loadTopicByName(str);

			if (topic != null) {
				Set<String> set = splitString(topic.getProp(), ",");
				topicToWhiteList.put(str, set);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("add topic %s 's proposal to whitelist %s", str, set));
				}
			} else {
				topic = getTopic(str, 0L);
				int status = saveTopic(topic);

				if (status == 0) {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Save topic %s to topic collection successfully.", str));
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Save topic %s to topic collection failed.", str));
					}
				}

			}

		}
	}

	private Set<String> splitString(String source, String delimitor) {
		String[] prop = source.split(delimitor);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

}
