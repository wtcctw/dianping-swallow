package com.dianping.swallow.kafka;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午7:20:43
 */
public class MultiTimeTest implements TestRule{
	
	private int count = 1;
	
	public MultiTimeTest(){
		
	}
	
	public MultiTimeTest(int count){
		this.count = count;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			
			@Override
			public void evaluate() throws Throwable {
				for(int i=0; i < count ; i++){
					base.evaluate();
				}
			}
		};
	}

}
