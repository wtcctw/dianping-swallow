package com.dianping.swallow.consumerserver;

import org.junit.Before;

import com.dianping.swallow.AbstractSpringTest;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever;


/**
 * @author mengwenchao
 *
 * 2015年5月13日 下午2:24:23
 */
public class AbstractConsumerServerSpringTest extends AbstractSpringTest{


	protected MessageDAO<?> messageDao;
	
	protected MessageRetriever messageRetriever;
	
	protected ClusterManager clusterManager;
	
	@Before
	public void beforeAbstractConsumerServerSpringTest(){
		
		messageDao = getBean(MessageDAO.class);
		clusterManager = getBean(ClusterManager.class);
		
		messageDao.cleanMessage(getTopic(), null);
		messageDao.cleanMessage(getTopic(), getConsumerId());
		messageRetriever = getBean(MessageRetriever.class);
		
	}


	@Override
	protected String getApplicationContextFile() {
		
		return "applicationContext-consumerserver-test.xml";
	}

	
	
	protected void insertSwallowMessage(int count){
		
		for(int i=0;i<count;i++){
			
			SwallowMessage message = createMessage();
			messageDao.saveMessage(getTopic(), message);
		}
		
		sleep(100);//wait slave
	}
	
}
