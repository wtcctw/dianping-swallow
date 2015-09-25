package com.dianping.swallow.web.controller.filter.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月24日下午2:42:24
 */
@Component
public class ConsumerServerConfigureFilter implements Filter<TopicApplyDto, ConfigureFilterResult> {

	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ConfigureFilterResult result,
			FilterChain<TopicApplyDto, ConfigureFilterResult> chain) {

		Pair<String, ResponseStatus> pair = consumerServerResourceService.loadIdleConsumerServer();
		ResponseStatus responseStatus = pair.getSecond();
		if (responseStatus != ResponseStatus.SUCCESS) {
			result.setMessage(responseStatus.getMessage());
			result.setStatus(responseStatus.getStatus());
			return;
		}
		String consumerServerChosen = pair.getFirst();
		LionConfigure lionConfigure = result.getLionConfigure();
		if(lionConfigure == null){
			lionConfigure = new LionConfigure();
			result.setLionConfigure(lionConfigure);
		}
		lionConfigure.setConsumerServer(consumerServerChosen);
		if (logger.isInfoEnabled()) {
			logger.info("Pass ConsumerServerConfigure");
		}

		chain.doFilter(topicApplyDto, result, chain);

	}
	
	public void setConsumerServerResourceService(ConsumerServerResourceService consumerServerResourceService) {
		this.consumerServerResourceService = consumerServerResourceService;
	}

}
