package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicResourceDao;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

import freemarker.template.utility.StringUtil;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午7:34:53
 */
@Service("topicResourceService")
public class TopicResourceServiceImpl extends AbstractSwallowService implements TopicResourceService, Runnable {

	private static final String FACTORY_NAME = "TopicResourceServiceImpl";

	public static final String SWALLOW_TOPIC_WHITELIST_KEY = "swallow.topic.whitelist";

	public static final String SWALLOW_CONSUMER_SERVER_URI = "swallow.consumer.consumerServerURI";

	@Autowired
	private TopicResourceDao topicResourceDao;

	@Autowired
	private TopicWhiteList topicWhiteList;

	private ConfigCache configCache;

	private Map<String, Set<String>> topicToAdministrator = new ConcurrentHashMap<String, Set<String>>();

	private ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor(ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	@PostConstruct
	public void executeCacheTopicToAdministrator(){
		
		scheduledExecutorService.scheduleAtFixedRate(this, 5, 5, TimeUnit.MINUTES);
		logger.info("Init configCache successfully.");
	}
	
	@Override
	protected void doInitialize() throws Exception {

		try {
			configCache = ConfigCache.getInstance();
			configCache.getProperty(SWALLOW_TOPIC_WHITELIST_KEY);
			Set<String> whiltlist = topicWhiteList.getTopics();
			for (String wl : whiltlist) {
				updateTopicAdministrator(wl, new HashSet<String>());
			}
			// 申请topic运行后就不需要监听了
			configCache.addChange(this);

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

			for (int i = whitelist.length - 1; i >= 0; i--) {
				String wl = whitelist[i];
				if (StringUtils.isNotBlank(wl) && topicResourceDao.findByTopic(wl) == null) {
					TopicResource topicResource = buildTopicResource(wl, null);
					try {
						boolean status = this.insert(topicResource);
						if (logger.isInfoEnabled() && status) {
							logger.info(String.format("Save topic %s to database", wl));
						} else if (logger.isErrorEnabled() && !status) {
							logger.error(String.format("Save topic %s to database fail with status %d", wl, status));
							continue;
						}
						topicToAdministrator.put(wl, new HashSet<String>());
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
	public boolean insert(TopicResource topicResource) {

		return topicResourceDao.insert(topicResource);
	}

	@Override
	public boolean update(TopicResource topicResource) {

		String topic = topicResource.getTopic();
		String proposal = topicResource.getAdministrator();
		String[] proposalArray = proposal.split(",");
		Set<String> proposalSet = new HashSet<String>(Arrays.asList(proposalArray));
		topicToAdministrator.put(topic, proposalSet);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Update cache topicToWhiteList of topic %s administrator to %s", topic, proposal));
		}
		return topicResourceDao.update(topicResource);
	}

	@Override
	public int remove(String topic) {

		return topicResourceDao.remove(topic);
	}

	@Override
	public Pair<Long, List<TopicResource>> findByTopics(int offset, int limit, String... topics) {

		return topicResourceDao.findByTopics(offset, limit, topics);
	}

	@Override
	public TopicResource findByTopic(String topic) {

		return topicResourceDao.findByTopic(topic);
	}

	@Override
	public Pair<Long, List<TopicResource>> find(int offset, int limit, String topic, String producerIp) {

		return topicResourceDao.find(offset, limit, topic, producerIp);
	}

	@Override
	public TopicResource findDefault() {

		return topicResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<TopicResource>> findTopicResourcePage(int offset, int limit) {

		return topicResourceDao.findTopicResourcePage(offset, limit);
	}

	@Override
	public List<TopicResource> findAll() {

		return topicResourceDao.findAll();
	}

	@Override
	public Pair<Long, List<TopicResource>> findByServer(int offset, int limit, String producerIp) {

		return topicResourceDao.findByServer(offset, limit, producerIp);
	}

	@Override
	public Pair<Long, List<TopicResource>> findByAdministrator(int offset, int limit, String administrator) {

		return topicResourceDao.findByAdministrator(offset, limit, administrator);
	}

	@Override
	public Map<String, Set<String>> loadCachedTopicToAdministrator() {

		return this.topicToAdministrator;
	}

	private TopicResource cacheTopicToAdministrator(String str, Set<String> defaultSet) {

		if (StringUtils.isBlank(str)) {
			return null;
		}
		TopicResource topicResource = findByTopic(str);

		if (topicResource != null) {
			Set<String> set = new HashSet<String>();
			String admin = topicResource.getAdministrator();
			if(StringUtils.isBlank(admin)){
				if(defaultSet != null && !defaultSet.isEmpty()){
					set = defaultSet;
					topicResource.setAdministrator(StringUtils.join(set, ","));
				}
			}else{
				set = splitString(admin, ",");
			}

			topicToAdministrator.put(str, set);
		} else {
			topicToAdministrator.put(str, defaultSet == null ? new HashSet<String>() : defaultSet);
		}

		return topicResource;
	}

	@Override
	public boolean updateTopicAdministrator(String str, Set<String> defaultSet) {
		// 先缓存再插入
		TopicResource topicResource = cacheTopicToAdministrator(str, defaultSet);

		if (topicResource == null) {
			topicResource = buildTopicResource(str, defaultSet);
			boolean status = this.insert(topicResource);

			if (status) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Save topic %s to topic collection successfully.", str));
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Save topic %s to topic collection failed.", str));
				}
			}

			return status;
		}else{
			this.update(topicResource);
		}

		return true;

	}

	private Set<String> splitString(String source, String delimitor) {
		String[] prop = source.split(delimitor);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

	@Override
	public TopicResource buildTopicResource(String topic, Set<String> adminSet) {

		Long id = System.currentTimeMillis();
		TopicResource topicResource = new TopicResource();
		if (adminSet != null) {
			topicResource.setAdministrator(StringUtils.join(adminSet, ","));
		} else {
			topicResource.setAdministrator("");
		}
		topicResource.setConsumerAlarm(Boolean.TRUE);
		topicResource.setProducerAlarm(Boolean.TRUE);
		topicResource.setProducerIpInfos(new ArrayList<IpInfo>());
		topicResource.setProducerApplications(new ArrayList<String>());
		topicResource.setTopic(topic);
		topicResource.setCreateTime(new Date());
		topicResource.setId(id.toString());

		TopicResource defaultTopicResource = topicResourceDao.findDefault();
		if (defaultTopicResource == null) {
			throw new RuntimeException("No default TopicResource configuration");
		}
		topicResource.setProducerAlarmSetting(defaultTopicResource.getProducerAlarmSetting());
		return topicResource;
	}

	@Override
	public void run() {

		try {
			SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "updateTopicToWhiteList");
			catWrapper.doAction(new SwallowAction() {
				@Override
				public void doAction() throws SwallowException {

					Set<String> whiltlist = topicWhiteList.getTopics();
					for (String wl : whiltlist) {
						cacheTopicToAdministrator(wl, new HashSet<String>());
					}

				}
			});
		} catch (Throwable th) {
			logger.error("[startUpdateTopicToWhiteList]", th);
		} finally {

		}
	}

}
