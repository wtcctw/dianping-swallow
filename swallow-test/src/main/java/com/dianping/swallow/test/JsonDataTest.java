package com.dianping.swallow.test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;

/**
 * @author mengwenchao
 *
 * 2015年3月19日 下午6:05:34
 */
public class JsonDataTest extends AbstractConsumerTest {
	

	@Test
	public void testSendData() throws Throwable{
		
		Data data = createData();
		final AtomicReference<Data> realData = new AtomicReference<JsonDataTest.Data>();
		final AtomicReference<Throwable> result = new AtomicReference<Throwable>();

		Consumer c = createConsumer(topic, getConsumerId());
		c.setListener(new MessageListener() {
			
			@Override
			public void onMessage(Message msg) throws BackoutMessageException {
				try{
					realData.set(msg.transferContentToBean(Data.class));
				}catch(Throwable th){
					logger.error("[error transform data]", th);
					result.set(th);
				}
			}
		});
		c.start();

		sendMessage(topic, data);

		waitForListernToComplete(1);
		if(result.get() != null){
			throw result.get();
		}
		
		Assert.assertEquals(data, realData.get());
		
		
	}
	
	private Data createData() {
		Data data = new Data();
		data.setName("dataTest");
		data.setTime(new Date());
		return data;
	}



	public static class Data {
		
		private String name;
		private Date time;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getTime() {
			return time;
		}
		public void setTime(Date time) {
			this.time = time;
		}

		@Override
		public boolean equals(Object obj) {
			
			if(obj == null || !(obj instanceof Data)){
				return false;
			}
			
			Data cmp = (Data) obj;
			return cmp.name.equals(name) && cmp.time.equals(time);
		}

		@Override
		public String toString() {
			return name + "," + time;
		}
	}
}
