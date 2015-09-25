package com.dianping.swallow.web.controller.filter.lion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午10:20:13
 */
public class TopicCfgLionFilterTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	private TopicCfgLionFilter lionEditor;
	
	private LionFilterEntity lionFilterEntity;
	
	private LionFilterChain lionFilterChain = new LionFilterChain();
	
	@Before
	public void setUp() throws Exception {
		
		TopicCfgLionFilter topicCfgLionFilter = new TopicCfgLionFilter();
		
		lionFilterEntity = new LionFilterEntity();
		lionFilterEntity.setTest(Boolean.TRUE);
		lionFilterEntity.setTopic("swallow-test");
		
		LionConfigure lionConfigure = new LionConfigure();
		lionConfigure.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		lionConfigure.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		lionConfigure.setSize4sevenday(500);
		
		lionFilterEntity.setLionConfigure(lionConfigure);
		
		lionEditor = topicCfgLionFilter;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		
		String topic = lionFilterEntity.getTopic();
		String key = "swallow.topiccfg." + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		String mongoURL = "mongodb://" + lionFilterEntity.getLionConfigure().getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionFilterEntity.getLionConfigure().getSize4sevenday());
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		String value = jsonBinder.toJson(mongoConfigBean);
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

		lionFilterChain.addFilter(lionEditor);
	}

	@Test
	public void test() {
		
		LionFilterResult result = new LionFilterResult();
		lionFilterChain.doFilter(lionFilterEntity, result, lionFilterChain);
		Assert.assertTrue(result.getStatus() == 0);
		
	}

}


