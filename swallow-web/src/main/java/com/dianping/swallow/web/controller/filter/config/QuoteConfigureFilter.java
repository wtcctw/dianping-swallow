package com.dianping.swallow.web.controller.filter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;

/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:45:26
 */
@Component
public class QuoteConfigureFilter implements Filter<TopicApplyDto, ConfigureFilterResult> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ConfigureFilterResult result,
			FilterChain<TopicApplyDto, ConfigureFilterResult> chain) {

		float amount = topicApplyDto.getAmount();
		int size = topicApplyDto.getSize();
		int size4sevenday = (int) (amount * size * 7 * 10);
		
		// size4sevenday取500的倍数
		int mod = size4sevenday % 500;
		size4sevenday = (mod != 0) ? (size4sevenday / 500 + 1) * 500 : size4sevenday;
		LionConfigure lionConfigure = result.getLionConfigure();
		if(lionConfigure == null){
			lionConfigure = new LionConfigure();
			result.setLionConfigure(lionConfigure);
		}
		lionConfigure.setSize4sevenday(size4sevenday);
		if(logger.isInfoEnabled()){
			logger.info("Pass QuoteConfigure");
		}

		chain.doFilter(topicApplyDto, result, chain);

	}

}
