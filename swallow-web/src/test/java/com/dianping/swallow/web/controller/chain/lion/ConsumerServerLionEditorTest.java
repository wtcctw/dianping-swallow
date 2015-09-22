package com.dianping.swallow.web.controller.chain.lion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.chain.config.Configure;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午10:20:13
 */
public class ConsumerServerLionEditorTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private ConsumerServerResourceService consumerServerResourceService;
	
	private ConsumerServerLionEditor lionEditor;
	
	private LionConfigBean lionConfigBean;
	
	@Before
	public void setUp() throws Exception {
		
		ConsumerServerLionEditor consumerServerLionEditor = new ConsumerServerLionEditor();
		
		Configure.ConfigureResult configureResult = new Configure.ConfigureResult();
		configureResult.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		configureResult.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		configureResult.setSize4servenday(500);
		configureResult.setResponseStatus(ResponseStatus.SUCCESS);
		
		lionConfigBean = new LionConfigBean();
		lionConfigBean.setTest(Boolean.TRUE);
		lionConfigBean.setTopic("swallow-test");
		lionConfigBean.setConfigureResult(configureResult);
		
		lionEditor = consumerServerLionEditor;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		lionEditor.setConsumerServerResourceService(consumerServerResourceService);

		String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
		Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();
		
		String key = "swallow.consumer.consumerServerURI";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(consumerServerConfig).append(";\n").append(lionConfigBean.getTopic()).append("=")
				.append(configureResult.getConsumerServer());
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());
	}

	@Test
	public void test() {
		
		Assert.assertTrue(lionEditor.editLion(lionConfigBean) == ResponseStatus.SUCCESS);
	}

}
