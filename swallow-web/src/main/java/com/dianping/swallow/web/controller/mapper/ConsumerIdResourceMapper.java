package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.ConsumerIdResourceDto;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;


/**
 * @author mingdongli
 *
 * 2015年7月13日下午2:56:41
 */
public class ConsumerIdResourceMapper {
	
	private static final String DELIMITOR = ",";
	
	public static ConsumerIdResource toConsumerIdResource(ConsumerIdResourceDto dto) {
		
		ConsumerIdResource consumerIdResource = new ConsumerIdResource();
		
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();
		
		QPSAlarmSetting sendQPSAlarmSetting = new QPSAlarmSetting();
		
		sendQPSAlarmSetting.setPeak(dto.getSendpeak());
		sendQPSAlarmSetting.setValley(dto.getSendvalley());
		sendQPSAlarmSetting.setFluctuation(dto.getSendfluctuation());
		sendQPSAlarmSetting.setFluctuationBase(dto.getSendfluctuationBase());
		consumerBaseAlarmSetting.setSendQpsAlarmSetting(sendQPSAlarmSetting);
		
		QPSAlarmSetting ackQPSAlarmSetting = new QPSAlarmSetting();
		
		ackQPSAlarmSetting.setPeak(dto.getAckpeak());
		ackQPSAlarmSetting.setValley(dto.getAckvalley());
		ackQPSAlarmSetting.setFluctuation(dto.getAckfluctuation());
		ackQPSAlarmSetting.setFluctuationBase(dto.getAckfluctuationBase());
		consumerBaseAlarmSetting.setAckQpsAlarmSetting(ackQPSAlarmSetting);
		
		consumerBaseAlarmSetting.setSendDelay(dto.getSenddelay());
		consumerBaseAlarmSetting.setAckDelay(dto.getAckdelay());
		consumerBaseAlarmSetting.setAccumulation(dto.getAccumulation());
		
		consumerIdResource.setConsumerAlarmSetting(consumerBaseAlarmSetting);
		consumerIdResource.setConsumerId(dto.getConsumerId());
		consumerIdResource.setTopic(dto.getTopic());
		consumerIdResource.setId(dto.getId());
		consumerIdResource.setAlarm(dto.isAlarm());
		
		List<String> idList = new ArrayList<String>();
		String whiteList = dto.getConsumerIp();
		
		if (StringUtils.isNotBlank(whiteList)) {
			String[] whiteLists = whiteList.split(DELIMITOR);
			for (String wl : whiteLists) {
				if (!idList.contains(wl)) {
					idList.add(wl);
				}
			}
		}
		
		consumerIdResource.setConsumerIps(idList);
		
		return consumerIdResource;
	}

	public static ConsumerIdResourceDto toConsumerIdResourceDto(ConsumerIdResource consumerIdResource) {
		
		ConsumerIdResourceDto dto = new ConsumerIdResourceDto();
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = consumerIdResource.getConsumerAlarmSetting();
		
		QPSAlarmSetting sendQPSAlarmSetting = consumerBaseAlarmSetting.getSendQpsAlarmSetting();
		dto.setSendpeak(sendQPSAlarmSetting.getPeak());
		dto.setSendvalley(sendQPSAlarmSetting.getValley());
		dto.setSendfluctuation(sendQPSAlarmSetting.getFluctuation());
		dto.setSendfluctuationBase(sendQPSAlarmSetting.getFluctuationBase());
		
		QPSAlarmSetting ackQPSAlarmSetting = consumerBaseAlarmSetting.getAckQpsAlarmSetting();
		dto.setAckpeak(ackQPSAlarmSetting.getPeak());
		dto.setAckvalley(ackQPSAlarmSetting.getValley());
		dto.setAckfluctuation(ackQPSAlarmSetting.getFluctuation());
		dto.setAckfluctuationBase(ackQPSAlarmSetting.getFluctuationBase());
		
		dto.setSenddelay(consumerBaseAlarmSetting.getSendDelay());
		dto.setAckdelay(consumerBaseAlarmSetting.getAckDelay());
		dto.setAccumulation(consumerBaseAlarmSetting.getAccumulation());
		
		dto.setConsumerId(consumerIdResource.getConsumerId());
		dto.setTopic(consumerIdResource.getTopic());
		dto.setId(consumerIdResource.getId());
		dto.setAlarm(consumerIdResource.isAlarm());
		
		List<String> list = consumerIdResource.getConsumerIps();
		dto.setConsumerIp(StringUtils.join(list, DELIMITOR));
		
		return dto;
	}

}
