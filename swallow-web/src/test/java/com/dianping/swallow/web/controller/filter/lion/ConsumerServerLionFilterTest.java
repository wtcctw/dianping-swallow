package com.dianping.swallow.web.controller.filter.lion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午10:20:13
 */
public class ConsumerServerLionFilterTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private ConsumerServerResourceService consumerServerResourceService;
	
	private ConsumerServerLionFilter lionEditor;
	
	private LionFilterEntity lionFilterEntity;
	
	private LionFilterChain lionFilterChain = new LionFilterChain();
	
	@Before
	public void setUp() throws Exception {
		
		ConsumerServerLionFilter consumerServerLionEditor = new ConsumerServerLionFilter();
		
		lionFilterEntity = new LionFilterEntity();
		lionFilterEntity.setTest(Boolean.TRUE);
		lionFilterEntity.setTopic("swallow-test");
		
		LionConfigure lionConfigure = new LionConfigure();
		lionConfigure.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		lionConfigure.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		lionConfigure.setSize4sevenday(500);
		
		lionFilterEntity.setLionConfigure(lionConfigure);
		
		lionEditor = consumerServerLionEditor;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		lionEditor.setConsumerServerResourceService(consumerServerResourceService);

		String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
		Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();
		
		String key = "swallow.consumer.consumerServerURI";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(consumerServerConfig).append(";\n").append(lionFilterEntity.getTopic()).append("=")
				.append(lionConfigure.getConsumerServer());
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());
		
		lionFilterChain.addFilter(lionEditor);
	}

	@Test
	public void test() {
		
		LionFilterResult result = new LionFilterResult();
		lionFilterChain.doFilter(lionFilterEntity, result, lionFilterChain);
		Assert.assertTrue(result.getStatus() == 0);
		
	}

}

