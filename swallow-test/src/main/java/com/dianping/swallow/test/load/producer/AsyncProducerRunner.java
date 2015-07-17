package com.dianping.swallow.test.load.producer;


import com.dianping.swallow.producer.ProducerMode;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class AsyncProducerRunner extends AbstractProducerLoadTest{

	@Override
	protected ProducerMode getProducerMode() {
		
		return ProducerMode.ASYNC_MODE;
	}
	
	public static void main(String []argc) throws Exception{
		
		parseArgs(argc);
		new AsyncProducerRunner().start();
		
	}

}