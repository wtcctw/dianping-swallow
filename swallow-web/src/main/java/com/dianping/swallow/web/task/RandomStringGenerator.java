package com.dianping.swallow.web.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author mingdongli
 *
 * 2015年6月10日上午10:35:36
 */
@Component
public class RandomStringGenerator {

	private static final String BASESTRING = "abcdefghijklmnopqrstuvwxyz";
	
	private static final int LENGTH = 32;
	
	private String randomString;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostConstruct
	private void init(){
		generateRandomString();
	}
	
	@Scheduled(cron = "0 0 3 * * ?")  //每天3点更新
	public void generateRandomString() {
		
		logger.info(String.format("Start generate random string for retransmit message"));
		setRandomString(getRandomString(LENGTH));
	}

	private int getRandom(int count) {
		
	    return (int) Math.round(Math.random() * (count));
	}

	private String getRandomString(int length) {
		StringBuffer sb = new StringBuffer();
		int len = BASESTRING.length();
		for (int i = 0; i < length; i++) {
			sb.append(BASESTRING.charAt(getRandom(len - 1)));
		}
		logger.info(String.format("Generage randow string [ %s ] for retransmit messages", sb.toString()));
		
		return sb.toString();
	}

	public String loadRandomString() {
		return randomString;
	}

	public void setRandomString(String randomString) {
		this.randomString = randomString;
	}
	
}
