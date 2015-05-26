package com.dianping.swallow.test.load.producer;

import java.io.IOException;

import com.dianping.swallow.producer.ProducerMode;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class AsyncProducerRunner extends AbstractProducerLoadTest{

	@Override
	protected ProducerMode getProducerMode() {
		
		return ProducerMode.ASYNC_MODE;
	}
	
	public static void main(String []argc) throws InterruptedException, IOException{
		
		parseArgs(argc);
		new AsyncProducerRunner().start();
		
	}

}