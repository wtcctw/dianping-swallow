package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.ConsumerIdAlarmSettingDto;
import com.dianping.swallow.web.controller.dto.TopicAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

/**
 * 
 * @author mingdongli
 *
 *         2015年7月14日上午10:40:36
 */
public class TopicAlarmSettingMapper {

	private static final String DELIMITOR = ",";

	public static TopicAlarmSetting toTopicAlarmSetting(TopicAlarmSettingDto dto) {
		

		TopicAlarmSetting alarmSetting = new TopicAlarmSetting();

		List<String> consumerIdWhiteList = new ArrayList<String>();

		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();

		ProducerBaseAlarmSetting producerBaseAlarmSetting = new ProducerBaseAlarmSetting();

		QPSAlarmSetting producerQPSAlarmSetting = new QPSAlarmSetting();
		
		producerQPSAlarmSetting.setPeak(dto.getProducerpeak());
		producerQPSAlarmSetting.setFluctuation(dto.getProducerfluctuation());
		producerQPSAlarmSetting.setValley(dto.getProducervalley());
		producerBaseAlarmSetting.setQpsAlarmSetting(producerQPSAlarmSetting);
		producerBaseAlarmSetting.setDelay(dto.getProducerdelay());
		
		QPSAlarmSetting consumerSendQPSAlarmSetting = new QPSAlarmSetting();
		consumerSendQPSAlarmSetting.setPeak(dto.getConsumersendpeak());
		consumerSendQPSAlarmSetting.setValley(dto.getConsumersendvalley());
		consumerSendQPSAlarmSetting.setFluctuation(dto.getConsumersendfluctuation());
		consumerBaseAlarmSetting.setSenderQpsAlarmSetting(consumerSendQPSAlarmSetting);

		QPSAlarmSetting consumerAckQPSAlarmSetting = new QPSAlarmSetting();
		consumerAckQPSAlarmSetting.setPeak(dto.getConsumerackpeak());
		consumerAckQPSAlarmSetting.setValley(dto.getConsumerackvalley());
		consumerAckQPSAlarmSetting.setFluctuation(dto.getConsumerackfluctuation());
		consumerBaseAlarmSetting.setSenderQpsAlarmSetting(consumerAckQPSAlarmSetting);
		
		consumerBaseAlarmSetting.setAckQpsAlarmSetting(consumerAckQPSAlarmSetting);
		consumerBaseAlarmSetting.setSenderDelay(dto.getConsumersenddelay());
		consumerBaseAlarmSetting.setAckDelay(dto.getConsumerackdelay());
		consumerBaseAlarmSetting.setAccumulation(dto.getConsumerfluctuation());
		
		alarmSetting.setConsumerAlarmSetting(consumerBaseAlarmSetting);
		alarmSetting.setProducerAlarmSetting(producerBaseAlarmSetting);
		
		String whiteList = dto.getWhitelist();
		String[] whiteLists = whiteList.split(DELIMITOR);
		for(String wl : whiteLists){
			if(!consumerIdWhiteList.contains(wl)){
				consumerIdWhiteList.add(wl);
			}
		}
		
		alarmSetting.setConsumerIdWhiteList(consumerIdWhiteList);
		alarmSetting.setTopicName(dto.getTopic());
		
		return alarmSetting;
	}

	public static TopicAlarmSettingDto toTopicAlarmSettingDto(TopicAlarmSetting alarmSetting) {

		TopicAlarmSettingDto dto = new TopicAlarmSettingDto();
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = alarmSetting.getConsumerAlarmSetting();

		QPSAlarmSetting sendQPSAlarmSetting = consumerBaseAlarmSetting.getSenderQpsAlarmSetting();
		dto.setConsumersendpeak(sendQPSAlarmSetting.getPeak());
		dto.setConsumersendvalley(sendQPSAlarmSetting.getValley());
		dto.setConsumersendfluctuation(sendQPSAlarmSetting.getFluctuation());
		
		QPSAlarmSetting ackQPSAlarmSetting = consumerBaseAlarmSetting.getAckQpsAlarmSetting();
		dto.setConsumerackpeak(ackQPSAlarmSetting.getPeak());
		dto.setConsumerackvalley(ackQPSAlarmSetting.getValley());
		dto.setConsumerackfluctuation(ackQPSAlarmSetting.getFluctuation());
		
		dto.setConsumersenddelay(consumerBaseAlarmSetting.getSenderDelay());
		dto.setConsumerackdelay(consumerBaseAlarmSetting.getAckDelay());
		dto.setConsumeraccumulation(consumerBaseAlarmSetting.getAccumulation());
		
		ProducerBaseAlarmSetting producerBaseAlarmSetting = alarmSetting.getProducerAlarmSetting();
		
		QPSAlarmSetting producerQPSAlarmSetting = producerBaseAlarmSetting.getQpsAlarmSetting();
		dto.setProducerpeak(producerQPSAlarmSetting.getPeak());
		dto.setProducervalley(producerQPSAlarmSetting.getValley());
		dto.setProducerfluctuation(producerQPSAlarmSetting.getFluctuation());
		dto.setProducerdelay(producerBaseAlarmSetting.getDelay());
		
		List<String> whiteList = alarmSetting.getConsumerIdWhiteList();
		dto.setWhitelist(StringUtils.join(whiteList, DELIMITOR));
		dto.setTopic(alarmSetting.getTopicName());

		return dto;
	}

}
