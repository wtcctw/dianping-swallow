package com.dianping.swallow.web.util;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 * @author qiyin
 *
 * 2015年9月17日 下午8:25:19
 */
public class CountDownLatchUtil {

	private static final Logger logger = LogManager.getLogger(CountDownLatchUtil.class);

	public static CountDownLatch createCountDownLatch(int totalCount) {
		return new CountDownLatch(totalCount);
	}

	public static void await(CountDownLatch countDownLatch) {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.error("[await] interrupted.", e);
		}
	}
}
