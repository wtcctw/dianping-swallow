package com.dianping.swallow.web.controller.chain.config;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午8:12:32
 */
@Component
public class QuoteConfigure extends AbstractConfigure implements Configure{

	public QuoteConfigure(){
		super();
	}

	public QuoteConfigure(Configure nextSuccessor) {
		super(nextSuccessor);
	}
	
	@Override
	public void buildConfigure(TopicApplyDto topicApplyDto, ConfigureResult configureResult) {
		
		float amount = topicApplyDto.getAmount();
		int size = topicApplyDto.getSize();
		int size4sevenday = (int) (amount * size * 7 * 10);
		
		// size4sevenday取500的倍数
		int mod = size4sevenday % 500;
		size4sevenday = (mod != 0) ? (size4sevenday / 500 + 1) * 500 : size4sevenday;
		configureResult.setSize4servenday(size4sevenday);
		if(logger.isInfoEnabled()){
			logger.info("Pass QuoteConfigure");
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
