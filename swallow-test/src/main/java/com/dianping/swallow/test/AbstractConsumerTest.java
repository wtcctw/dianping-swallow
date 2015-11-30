package com.dianping.swallow.test;

import org.junit.Before;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;


/**
 * @author mengwenchao
 *
 * 2015年3月18日 下午5:34:49
 */
public abstract class AbstractConsumerTest extends AbstractSwallowTest{

	private String consumerId = "st";
	
	
	@Before
	public void beforeAbstractConsumerTest() throws SendFailedException, RemoteServiceInitFailedException{
		
		if(isCleanData()){
			try{
				mdao.cleanMessage(getTopic(), getConsumerId());
				mdao.cleanMessage(getTopic(), null);
			}catch(UnsupportedOperationException e){
				
			}
		}
		
		mdao.cleanAck(getTopic(), getConsumerId());
		mdao.cleanAck(getTopic(), getConsumerId(), true);
		
		//初始消息，解决由本地时间和服务器时间不一致照成的消息数量不一致的问题
		sendMessage(1, getTopic());
		cleanSendMessageCount();
	}

	

	protected boolean isCleanData() {
		return true;
	}



	protected String getConsumerId() {
		
		return consumerId + "-" + testName.getMethodName();
	}

	
}
