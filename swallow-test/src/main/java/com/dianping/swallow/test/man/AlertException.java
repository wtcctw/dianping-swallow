package com.dianping.swallow.test.man;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.exception.SwallowAlertException;

/**
 * @author mengwenchao
 *
 * 2015年9月11日 下午4:24:43
 */
public class AlertException {

	
	protected Logger logger = LogManager.getLogger(getClass());
	
	private static int exceptionCount = 1;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		if(args.length >= 1){
			exceptionCount = Integer.parseInt(args[0]);
		}
		
		new AlertException().start();
		
		TimeUnit.SECONDS.sleep(30);
	}

	private void start() {
		
		for(int i=0 ; i<exceptionCount ; i++){

			logger.error("error message", new SwallowAlertException("alert 112345"));
		}
		
	}

}
