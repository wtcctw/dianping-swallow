package com.dianping.swallow.test.man;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @author mengwenchao
 *
 * 2015年7月9日 下午3:28:17
 */
public class SimpleTest implements MessageListener {
	
	
	public static void main(String []argc){
		
		new SimpleTest().start();
		
	}

	private void start() {
		
		Consumer consumer  = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("swallow-test-integrated"), "consumerId");
		consumer.setListener(this);
		consumer.start();
		
	}

	@Override
	public void onMessage(Message msg) throws BackoutMessageException {
		System.out.println(msg);
	}

}
