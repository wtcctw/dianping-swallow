package com.dianping.swallow.web.util;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author qiyin
 *
 * 2015年9月17日 下午8:25:19
 */
public class CountDownLatchUtil {

	private static final Logger logger = LoggerFactory.getLogger(CountDownLatchUtil.class);

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
