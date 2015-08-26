package com.dianping.swallow.web.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.dao.TopicResourceDao;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicResourceService;

import freemarker.template.utility.StringUtil;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午7:34:53
 */
@Service("topicResourceService")
public class TopicResourceServiceImpl extends AbstractSwallowService implements
		TopicResourceService {

	private static final String SWALLOW_TOPIC_WHITELIST_KEY = "swallow.topic.whitelist";

	@Autowired
	private TopicResourceDao topicResourceDao;

	private ConfigCache configCache;

	private Map<String, Set<String>> topicToWhiteList = new ConcurrentHashMap<String, Set<String>>();

	@PostConstruct
	void initLionConfig() {

		try {
			configCache = ConfigCache.getInstance();
			String key = configCache.getProperty(SWALLOW_TOPIC_WHITELIST_KEY);
			Set<String> whiltlist = splitString(key, ";");
			for (String wl : whiltlist) {
				cacheTopicToWhiteList(wl);
			}
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
				logger.info("[onChange][" + SWALLOW_TOPIC_WHITELIST_KEY + "]"
						+ value);
			}

			String[] whitelist = StringUtil.split(value, ';');

			for (String wl : whitelist) {
				if (StringUtils.isNotBlank(wl)
						&& topicResourceDao.findByTopic(wl) == null) {
					TopicResource topicResource = buildTopicResource(wl);
					try {
						boolean status = this.insert(topicResource);
						if (logger.isInfoEnabled() && status) {
							logger.info(String.format(
									"Save topic %s to database", wl));
						} else if (logger.isErrorEnabled() && !status) {
							logger.error(String
									.format("Save topic %s to database fail with status %d",
											wl, status));
							continue;
						}
						topicToWhiteList.put(wl, new HashSet<String>());
						if (logger.isInfoEnabled()) {
							logger.info(String
									.format("Add topic %s to whitelist with empty proposal",
											wl));
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

		return topicResourceDao.update(topicResource);
	}

	@Override
	public int remove(String topic) {

		return topicResourceDao.remove(topic);
	}

	@Override
	public TopicResource findByTopic(String topic) {

		return topicResourceDao.findByTopic(topic);
	}

	@Override
	public TopicResource findDefault() {

		return topicResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<TopicResource>> findTopicResourcePage(
			TopicQueryDto topicQueryDto) {

		return topicResourceDao.findTopicResourcePage(topicQueryDto);
	}

	@Override
	public List<TopicResource> findAll() {

		return topicResourceDao.findAll();
	}

	@Override
	public Pair<Long, List<TopicResource>> findByServer(
			TopicQueryDto topicQueryDto) {

		return topicResourceDao.findByServer(topicQueryDto);
	}

	@Override
	public Map<String, Set<String>> loadCachedTopicToWhiteList() {

		return this.topicToWhiteList;
	}

	private void cacheTopicToWhiteList(String str) {

		if (StringUtils.isBlank(str)) {
			return;
		}
		TopicResource topicResource = this.findByTopic(str);

		if (topicResource != null) {
			Set<String> set = splitString(topicResource.getProp(), ",");
			topicToWhiteList.put(str, set);
			if (logger.isInfoEnabled()) {
				logger.info(String.format(
						"add topic %s 's proposal to whitelist %s", str, set));
			}
		} else {
			topicResource = buildTopicResource(str);
			boolean status = this.insert(topicResource);

			if (status) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"Save topic %s to topic collection successfully.",
							str));
				}
				topicToWhiteList.put(str, new HashSet<String>());
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"Save topic %s to topic collection failed.", str));
				}
			}

		}

	}

	private Set<String> splitString(String source, String delimitor) {
		String[] prop = source.split(delimitor);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

	@Override
	public TopicResource buildTopicResource(String topic) {

		Long id = System.currentTimeMillis();
		TopicResource topicResource = new TopicResource();
		topicResource.setProp("");
		topicResource.setConsumerAlarm(Boolean.TRUE);
		topicResource.setProducerAlarm(Boolean.TRUE);
		topicResource.setName(topic);
		topicResource.setCreateTime(new Date());
		topicResource.setId(id.toString());
		QPSAlarmSetting qPSAlarmSetting = new QPSAlarmSetting();
		ProducerBaseAlarmSetting producerBaseAlarmSetting = new ProducerBaseAlarmSetting();
		producerBaseAlarmSetting.setQpsAlarmSetting(qPSAlarmSetting);
		topicResource.setProducerAlarmSetting(producerBaseAlarmSetting);
		return topicResource;
	}

}
