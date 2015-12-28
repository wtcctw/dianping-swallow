package com.dianping.swallow.common.internal.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;



/**
 * @author mengwenchao
 *
 * 2015年11月12日 上午11:46:30
 */
public class TestSkipRule implements TestRule{
	
	public static final String TEST_SWIMELINE = "test";
	
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Override
	public Statement apply(final Statement base, Description description) {
		
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				if(environmentOk()){
					base.evaluate();
				}
			}
		};
		
		
	}

	private boolean environmentOk() {
		if(EnvUtil.isDev()){
			return true;
		}
		String swimeline = StringUtils.trimToNull(EnvUtil.getSwimeline());
		if(TEST_SWIMELINE.equals(swimeline)){
			return true;
		}
		logger.warn("[environmentOk][test skip][env not test, nor swimeline:" + TEST_SWIMELINE+ "]");
		return false;
	}

}
