package com.dianping.swallow.web.controller.mapper;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.TopicResourceDto;
import com.dianping.swallow.web.controller.utils.IpInfoUtils;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午4:51:03
 */
public class TopicResourceMapper {
	private static final String DELIMITOR = ",";

	public static TopicResource toTopicResource(TopicResourceDto dto) {
		
		TopicResource topicResource = new TopicResource();

		ProducerBaseAlarmSetting producerBaseAlarmSetting = new ProducerBaseAlarmSetting();
		
		QPSAlarmSetting sendQPSAlarmSetting = new QPSAlarmSetting();
		sendQPSAlarmSetting.setPeak(dto.getSendpeak());
		sendQPSAlarmSetting.setValley(dto.getSendvalley());
		sendQPSAlarmSetting.setFluctuation(dto.getSendfluctuation());
		sendQPSAlarmSetting.setFluctuationBase(dto.getSendfluctuationBase());
		
		producerBaseAlarmSetting.setQpsAlarmSetting(sendQPSAlarmSetting);
		producerBaseAlarmSetting.setDelay(dto.getDelay());
		
		topicResource.setProducerAlarmSetting(producerBaseAlarmSetting);
		
		topicResource.setConsumerAlarm(dto.isConsumerAlarm());
		topicResource.setProducerAlarm(dto.isProducerAlarm());
		topicResource.setId(dto.getId());
		topicResource.setTopic(dto.getTopic());
		topicResource.setAdministrator(dto.getAdministrator());

		return topicResource;
		
	}
	
	public static TopicResourceDto toTopicResourceDto(TopicResource topicResource) {
		
		TopicResourceDto topicResourceDto = new TopicResourceDto();
		ProducerBaseAlarmSetting producerBaseAlarmSetting = topicResource.getProducerAlarmSetting();
		QPSAlarmSetting sendQPSAlarmSetting = producerBaseAlarmSetting.getQpsAlarmSetting();
		
		topicResourceDto.setDelay(producerBaseAlarmSetting.getDelay());
		topicResourceDto.setSendpeak(sendQPSAlarmSetting.getPeak());
		topicResourceDto.setSendvalley(sendQPSAlarmSetting.getValley());
		topicResourceDto.setSendfluctuation(sendQPSAlarmSetting.getFluctuation());
		topicResourceDto.setSendfluctuationBase(sendQPSAlarmSetting.getFluctuationBase());
		
		topicResourceDto.setConsumerAlarm(topicResource.isConsumerAlarm());
		topicResourceDto.setProducerAlarm(topicResource.isProducerAlarm());
		topicResourceDto.setId(topicResource.getId());
		topicResourceDto.setTopic(topicResource.getTopic());
		topicResourceDto.setAdministrator(topicResource.getAdministrator());
		
		List<IpInfo> list = topicResource.getProducerIpInfos();
		Set<String> ips = IpInfoUtils.extractIps(list);
		topicResourceDto.setProducerServer(StringUtils.join(ips, DELIMITOR));
		
		return topicResourceDto;
	}
}
