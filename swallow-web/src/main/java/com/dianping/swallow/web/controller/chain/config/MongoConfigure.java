package com.dianping.swallow.web.controller.chain.config;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.MongoResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午7:52:11
 */
@Component
public class MongoConfigure extends AbstractConfigure implements Configure{

	@Resource(name = "mongoResourceService")
	private MongoResourceService mongoResourceService;

	public MongoConfigure(){
		super();
	}

	public MongoConfigure(Configure nextSuccessor) {
		super(nextSuccessor);
	}
	
	@Override
	public void buildConfigure(TopicApplyDto topicApplyDto, ConfigureResult configureResult) {

		String type = topicApplyDto.getType();
		MongoType mongoType = MongoType.findByType(type);
		MongoResource mongoResource = mongoResourceService.findIdleMongoByType(mongoType);
		
		if (mongoResource == null) {
			if(logger.isInfoEnabled()){
				logger.info("Fail MongoConfigure, No suitabe mongo");
			}
			configureResult.setResponseStatus(ResponseStatus.NOTEXIST);
			return;
		}else{
			String mongoChosen = mongoResource.getIp();
			if (StringUtils.isBlank(mongoChosen)) {
				if(logger.isInfoEnabled()){
					logger.info("Fail MongoConfigure, blank ip");
				}
				configureResult.setResponseStatus(ResponseStatus.INVALIDIP);
				return;
			}
			configureResult.setMongoServer(mongoChosen);
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
		
	}

	public void setMongoResourceService(MongoResourceService mongoResourceService) {
		this.mongoResourceService = mongoResourceService;
	}

}
