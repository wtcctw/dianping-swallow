package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerServerResourceDao;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.DateUtil;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午6:25:07
 */
@Service("consumerServerResourceService")
public class ConsumerServerResourceServiceImpl extends AbstractSwallowService implements ConsumerServerResourceService {

	private static final String BLANK_STRING = "";

	public static final String SWALLOW_CONSUMER_SERVER_URI = "swallow.consumer.consumerServerURI";

	@Autowired
	private ConsumerServerResourceDao consumerServerResourceDao;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Resource(name = "consumerServerStatsDataService")
	private ConsumerServerStatsDataService consumerServerStatsDataService;

	private ConfigCache configCache;

	private String consumerServerLionConfig;

	@PostConstruct
	void initLionConfig() {
		try {
			configCache = ConfigCache.getInstance();

			consumerServerLionConfig = configCache.getProperty(SWALLOW_CONSUMER_SERVER_URI);

			logger.info("Init configCache successfully.");
		} catch (LionException e) {
			logger.error("Erroe when init lion config", e);
		}
	}

	@Override
	public boolean insert(ConsumerServerResource consumerServerResource) {

		return consumerServerResourceDao.insert(consumerServerResource);
	}

	@Override
	public boolean update(ConsumerServerResource consumerServerResource) {

		return consumerServerResourceDao.update(consumerServerResource);
	}

	@Override
	public int remove(String ip) {

		return consumerServerResourceDao.remove(ip);
	}

	@Override
	public Pair<Long, List<ConsumerServerResource>> findConsumerServerResourcePage(int offset, int limit) {

		return consumerServerResourceDao.findConsumerServerResourcePage(offset, limit);
	}

	@Override
	public ServerResource findByIp(String ip) {

		return consumerServerResourceDao.findByIp(ip);
	}

	@Override
	public ServerResource findByHostname(String hostname) {

		return consumerServerResourceDao.findByHostname(hostname);
	}

	@Override
	public ServerResource findDefault() {

		return consumerServerResourceDao.findDefault();
	}

	@Override
	public List<ConsumerServerResource> findAll() {

		return consumerServerResourceDao.findAll();
	}

	@Override
	public ConsumerServerResource buildConsumerServerResource(String ip, String hostName, int port, String relatedIp,
			ServerType serverType) {
		ConsumerServerResource serverResource = new ConsumerServerResource();
		serverResource.setIp(ip);
		serverResource.setAlarm(true);
		serverResource.setHostname(hostName);
		serverResource.setPort(port);
		serverResource.setIpCorrelated(relatedIp);
		serverResource.setType(serverType);
		serverResource.setCreateTime(new Date());
		serverResource.setUpdateTime(new Date());
		ConsumerServerResource defaultResource = (ConsumerServerResource) findDefault();
		if (defaultResource == null) {
			serverResource.setAlarm(false);
			serverResource.setSendAlarmSetting(new QPSAlarmSetting());
			serverResource.setAckAlarmSetting(new QPSAlarmSetting());
		} else {
			serverResource.setSendAlarmSetting(defaultResource.getSendAlarmSetting());
			serverResource.setAckAlarmSetting(defaultResource.getAckAlarmSetting());
		}
		return serverResource;
	}
	
	@Override
	public ConsumerServerResource buildConsumerServerResource(String ip) {
		ConsumerServerResource serverResource = new ConsumerServerResource();
		serverResource.setIp(ip);
		serverResource.setAlarm(true);
		serverResource.setCreateTime(new Date());
		serverResource.setUpdateTime(new Date());
		ConsumerServerResource defaultResource = (ConsumerServerResource) findDefault();
		if (defaultResource == null) {
			serverResource.setAlarm(false);
			serverResource.setSendAlarmSetting(new QPSAlarmSetting());
			serverResource.setAckAlarmSetting(new QPSAlarmSetting());
		} else {
			serverResource.setSendAlarmSetting(defaultResource.getSendAlarmSetting());
			serverResource.setAckAlarmSetting(defaultResource.getAckAlarmSetting());
		}
		return serverResource;
	}

	@Override
	public Pair<String, ResponseStatus> loadIdleConsumerServer() {

		List<ConsumerServerResource> consumerServerResources = findAll();
		List<String> masterips = new ArrayList<String>();
		for (ConsumerServerResource consumerServerResource : consumerServerResources) {
			if (consumerServerResource.getType() == ServerType.MASTER) {
				String ip = consumerServerResource.getIp();
				if (StringUtils.isNotBlank(ip) && !masterips.contains(ip)) {
					masterips.add(ip);
				}
			}
		}

		long originalStart = DateUtil.getYesterayStart();
		long originalStop = DateUtil.getYesterayStop();
		long startKey = AbstractRetriever.getKey(originalStart);
		long endKey = AbstractRetriever.getKey(originalStop);

		int count = 0;
		while (count < 5) {
			String bestMaster = consumerServerStatsDataService.findIdleConsumerServer(masterips, startKey, endKey);

			if (StringUtils.isBlank(bestMaster)) {
				count++;
				originalStart = DateUtil.getOneDayBefore(originalStart);
				originalStop = DateUtil.getOneDayBefore(originalStop);
				startKey = AbstractRetriever.getKey(originalStart);
				endKey = AbstractRetriever.getKey(originalStop);
			} else {
				for (ConsumerServerResource consumerServerResource : consumerServerResources) {
					String mip = consumerServerResource.getIp();
					if (StringUtils.isNotBlank(mip) && mip.equals(bestMaster)) {
						String ip = consumerServerResource.getIp();
						if (StringUtils.isNotBlank(ip) && !masterips.contains(ip)) {
							masterips.add(ip);
						}
					}
				}
				ConsumerServerResource ConsumerServerResource = (ConsumerServerResource) this.findByIp(bestMaster);
				int masterPort = ConsumerServerResource.getPort();
				String slaveIp = ConsumerServerResource.getIpCorrelated();
				ConsumerServerResource = (ConsumerServerResource) this.findByIp(slaveIp);
				if (ConsumerServerResource == null) {
					return new Pair<String, ResponseStatus>(BLANK_STRING, ResponseStatus.NOCONSUMERSERVER);
				}
				int slavePort = ConsumerServerResource.getPort();
				String ipCorrelated = ConsumerServerResource.getIpCorrelated();
				if (!bestMaster.equals(ipCorrelated)) {
					return new Pair<String, ResponseStatus>(BLANK_STRING, ResponseStatus.NOCONSUMERSERVER);
				}
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(bestMaster).append(":").append(masterPort).append(",").append(slaveIp).append(":")
						.append(slavePort);
				return new Pair<String, ResponseStatus>(stringBuilder.toString(), ResponseStatus.SUCCESS);
			}
		}

		return new Pair<String, ResponseStatus>(BLANK_STRING, ResponseStatus.NOCONSUMERSERVER);
	}

	@Override
	public String loadConsumerServerLionConfig() {

		return consumerServerLionConfig;
	}

	@Override
	public synchronized void setConsumerServerLionConfig(String consumerServerLionConfig) {

		this.consumerServerLionConfig = consumerServerLionConfig;
	}
}
