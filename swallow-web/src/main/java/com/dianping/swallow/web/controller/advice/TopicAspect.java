package com.dianping.swallow.web.controller.advice;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

/**
 * @author mingdongli
 *
 * 2015年7月21日下午5:46:15
 */
@Component
@Aspect
public class TopicAspect {
	
	@Resource(name = "consumerServerAlarmSettingService")
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService;
	
	@Resource(name = "producerServerAlarmSettingService")
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	private final static Log log = LogFactory.getLog(TopicAspect.class);
	
	@Pointcut("execution(* com.dianping.swallow.web.controller.TopicController.fetchTopicPage(..))")
	public void aspect(){	}
	
	@Around("aspect()")
	public Pair<Object, Object> around(ProceedingJoinPoint joinPoint){
		Object object = null;
		try {
			object = joinPoint.proceed();
			List<String> consumerTopics = consumerServerAlarmSettingService.findDefault().getTopicWhiteList();
			List<String> producerTopics = producerServerAlarmSettingService.findDefault().getTopicWhiteList();
			@SuppressWarnings("unchecked")
			Collection<String> common = CollectionUtils.intersection(consumerTopics, producerTopics);
			
			return new Pair<Object, Object>(object,common);
		} catch (Throwable e) {
			log.error("joinPoint throw exception",e);
		}
		return new Pair<Object, Object>(object,new ArrayList<String>());
	}
	
}