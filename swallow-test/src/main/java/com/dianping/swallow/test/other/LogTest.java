package com.dianping.swallow.test.other;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.test.AbstractUnitTest;

/**
 * @author mengwenchao
 * 
 *         2015年7月20日 下午3:46:34
 */
public class LogTest extends AbstractUnitTest {

	@Test
	public void testLog() {
		logger.info("nihao");

	}

	@Test
	public void testError() throws InterruptedException {

		final CatActionWrapper wrapper = new CatActionWrapper("Logtest", "testError");

		for (int i = 0; i < 10; i++) {
			
			executors.execute(new Runnable() {

				@Override
				public void run() {

					wrapper.doAction(new SwallowAction() {

						@Override
						public void doAction() throws SwallowException {
							
							throw new IllegalArgumentException("test");
						}
					});
				}

			});
		}
		
		TimeUnit.SECONDS.sleep(10);
	}
}
