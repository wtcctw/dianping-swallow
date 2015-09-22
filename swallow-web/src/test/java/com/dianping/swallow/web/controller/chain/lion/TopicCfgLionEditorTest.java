package com.dianping.swallow.web.controller.chain.lion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.chain.config.Configure;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午10:45:43
 */
public class TopicCfgLionEditorTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	private TopicCfgLionEditor lionEditor;
	
	private LionConfigBean lionConfigBean;
	
	@Before
	public void setUp() throws Exception {
		
		TopicCfgLionEditor topicCfgLionEditor = new TopicCfgLionEditor();
		
		Configure.ConfigureResult configureResult = new Configure.ConfigureResult();
		configureResult.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		configureResult.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		configureResult.setSize4servenday(500);
		configureResult.setResponseStatus(ResponseStatus.SUCCESS);
		
		lionConfigBean = new LionConfigBean();
		lionConfigBean.setTest(Boolean.TRUE);
		lionConfigBean.setTopic("swallow-test");
		lionConfigBean.setConfigureResult(configureResult);
		
		lionEditor = topicCfgLionEditor;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		
		String topic = lionConfigBean.getTopic();
		String key = "swallow.topiccfg." + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		String mongoURL = "mongodb://" + lionConfigBean.getConfigureResult().getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionConfigBean.getConfigureResult().getSize4servenday());

		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		String value = jsonBinder.toJson(mongoConfigBean);
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
	}

	@Test
	public void test() {
		
		Assert.assertTrue(lionEditor.editLion(lionConfigBean) == ResponseStatus.SUCCESS);
	}

}
