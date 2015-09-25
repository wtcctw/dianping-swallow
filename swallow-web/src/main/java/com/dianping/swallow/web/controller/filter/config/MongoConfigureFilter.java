package com.dianping.swallow.web.controller.filter.config;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.MongoResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:20:46
 */
@Component
public class MongoConfigureFilter implements  Filter<TopicApplyDto, ConfigureFilterResult>{
	
	@Resource(name = "mongoResourceService")
	private MongoResourceService mongoResourceService;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ConfigureFilterResult result,
			FilterChain<TopicApplyDto, ConfigureFilterResult> chain) {
		
		String type = topicApplyDto.getType();
		MongoType mongoType = MongoType.findByType(type);
		MongoResource mongoResource = mongoResourceService.findIdleMongoByType(mongoType);
		
		if (mongoResource == null) {
			if(logger.isInfoEnabled()){
				logger.info("Fail MongoConfigure, No suitabe mongo");
			}
			result.setMessage("not exist");
			result.setStatus(-19);
			return;
		}else{
			String mongoChosen = mongoResource.getIp();
			if (StringUtils.isBlank(mongoChosen)) {
				if(logger.isInfoEnabled()){
					logger.info("Fail MongoConfigure, blank ip");
				}
				result.setMessage("invalid ip mapping");
				result.setStatus(-16);
				return;
			}
			LionConfigure lionConfigure = result.getLionConfigure();
			if(lionConfigure == null){
				lionConfigure = new LionConfigure();
				result.setLionConfigure(lionConfigure);
			}
			lionConfigure.setMongoServer(mongoChosen);
			if(logger.isInfoEnabled()){
				logger.info("Pass MongoConfigure");
			}
			
			chain.doFilter(topicApplyDto, result, chain);
		}
		
	}
	
	public void setMongoResourceService(MongoResourceService mongoResourceService) {
		this.mongoResourceService = mongoResourceService;
	}

}
