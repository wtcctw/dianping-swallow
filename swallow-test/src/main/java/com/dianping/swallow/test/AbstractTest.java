package com.dianping.swallow.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 同时供压力测试和单元测试
 * @author mengwenchao
 *
 * 2015年11月30日 下午7:29:25
 */
public abstract class AbstractTest {
	
	@JsonIgnore
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@JsonIgnore
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected String createMessage(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<size;i++){
			sb.append("c");
		}
		return sb.toString();
	}
	

	protected void sleep(int miliSeconds) {
		
		if(logger.isDebugEnabled() && miliSeconds > 0){
			logger.debug("[sleep]" + miliSeconds);
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}
