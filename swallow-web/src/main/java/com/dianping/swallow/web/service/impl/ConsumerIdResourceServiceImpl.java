package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import jodd.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;

/**
 * @author mingdongli
 *
 *         2015年8月11日上午10:34:28
 */
@Service("consumerIdResourceService")
public class ConsumerIdResourceServiceImpl extends AbstractSwallowService implements ConsumerIdResourceService {

	@Autowired
	private ConsumerIdResourceDao consumerIdResourceDao;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	private AtomicBoolean minute = new AtomicBoolean(false);

	@PostConstruct
	void updateDashboardContainer() {

		consumerDataRetrieverWrapper.registerListener(this);
	}

	@Override
	public boolean insert(ConsumerIdResource consumerIdResource) {

		return consumerIdResourceDao.insert(consumerIdResource);
	}

	@Override
	public boolean update(ConsumerIdResource consumerIdResource) {

		return consumerIdResourceDao.update(consumerIdResource);
	}

	@Override
	public int remove(String topic, String consumerid) {

		return consumerIdResourceDao.remove(topic, consumerid);
	}

	@Override
	public List<ConsumerIdResource> findByConsumerId(String consumerid) {

		return consumerIdResourceDao.findByConsumerId(consumerid);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.findByTopic(consumerIdQueryDto);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> find(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.find(consumerIdQueryDto);
	}

	@Override
	public List<ConsumerIdResource> findAll(String... fields) {

		return consumerIdResourceDao.findAll(fields);
	}

	@Override
	public ConsumerIdResource findDefault() {

		return consumerIdResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.findConsumerIdResourcePage(consumerIdQueryDto);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findByConsumerIp(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.findByConsumerIp(consumerIdQueryDto);
	}

	@Override
	public void achieveMonitorData() {

		if (minute.get()) {
			try {
				flushConsumerIdMetaData();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when flush consumerid data to database.", e);
				}
			} finally {
				minute.compareAndSet(true, false);
			}
		} else {
			minute.compareAndSet(false, true);
		}

	}

	private void flushConsumerIdMetaData() {

		Set<String> topics = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL);
		for (String topic : topics) {
			Set<String> consumerids = consumerDataRetrieverWrapper.getKeyWithoutTotal(
					ConsumerDataRetrieverWrapper.TOTAL, topic);
			for (String cid : consumerids) {
				Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL,
						topic, cid);
				if(valid(topic, cid)){
					ConsumerIdQueryDto consumerIdQueryDto = new ConsumerIdQueryDto();
					consumerIdQueryDto.setConsumerId(cid);
					consumerIdQueryDto.setTopic(topic);
					Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceDao.find(consumerIdQueryDto);
					
					if(pair.getFirst() == 0){
						ConsumerIdResource consumerIdResource = buildConsumerIdResource(topic, cid, ips);
						consumerIdResourceDao.insert(consumerIdResource);
					}else{
						if(ips != null && !ips.isEmpty()){
							ConsumerIdResource consumerIdResource = pair.getSecond().get(0);
							consumerIdResource.setConsumerIps(new ArrayList<String>(ips));
							consumerIdResourceDao.insert(consumerIdResource);
						}
					}
				}
			}
		}
	}
	
	private boolean valid(String topic, String consumerId){
		
		if(StringUtil.isNotBlank(topic) && StringUtil.isNotBlank(consumerId)){
			return true;
		}else{
			return false;
		}
	}
	
	private ConsumerIdResource buildConsumerIdResource(String topic, String consumerId, Set<String> ips){
	
		ConsumerIdResource consumerIdResource = new ConsumerIdResource();
		consumerIdResource.setAlarm(Boolean.TRUE);
		consumerIdResource.setTopic(topic);
		consumerIdResource.setConsumerId(consumerId);
		
		List<String> ipList = null;
		
		if(ips == null){
			ipList = new ArrayList<String>();
		}else{
			ipList = new ArrayList<String>(ips);
		}
		
		consumerIdResource.setConsumerIps(ipList);
		
		ConsumerIdResource defaultResource = consumerIdResourceDao.findDefault();
		if(defaultResource == null){
			throw new RuntimeException("No default configuration for ConsumerIdResource");
		}
		consumerIdResource.setConsumerAlarmSetting(defaultResource.getConsumerAlarmSetting());
		
		return consumerIdResource;
	}

}
