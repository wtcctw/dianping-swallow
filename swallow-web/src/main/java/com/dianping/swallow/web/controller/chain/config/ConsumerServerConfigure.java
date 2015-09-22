package com.dianping.swallow.web.controller.chain.config;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午8:12:32
 */
@Component
public class ConsumerServerConfigure extends AbstractConfigure implements Configure{

	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	public ConsumerServerConfigure(){
		super();
	}

	public ConsumerServerConfigure(Configure nextSuccessor) {
		super(nextSuccessor);
	}
	
	@Override
	public void buildConfigure(TopicApplyDto topicApplyDto, ConfigureResult configureResult) {
		
		Pair<String, ResponseStatus> pair = consumerServerResourceService.loadIdleConsumerServer();
		ResponseStatus responseStatus = pair.getSecond();
		if(responseStatus != ResponseStatus.SUCCESS){
			configureResult.setResponseStatus(responseStatus);
			return;
		}
		String consumerServerChosen = pair.getFirst();
		configureResult.setConsumerServer(consumerServerChosen);
		if(logger.isInfoEnabled()){
			logger.info("Pass MongoConfigure");
		}
		
		if(nextSuccessor != null){
			nextSuccessor.buildConfigure(topicApplyDto, configureResult);
			return;
		}else{
			configureResult.setResponseStatus(ResponseStatus.SUCCESS);
			return;
		}

	}

	public void setConsumerServerResourceService(ConsumerServerResourceService consumerServerResourceService) {
		this.consumerServerResourceService = consumerServerResourceService;
	}

}
