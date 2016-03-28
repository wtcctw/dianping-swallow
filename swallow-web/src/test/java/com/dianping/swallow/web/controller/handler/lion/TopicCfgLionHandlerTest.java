package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.TopicApplyController;
import com.dianping.swallow.web.controller.handler.config.KafkaServerHandler;
import com.dianping.swallow.web.controller.handler.config.MongoServerHandler;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author mingdongli
 *         15/10/23 下午8:44
 */
public class TopicCfgLionHandlerTest extends MockTest {

    @Mock
    private LionUtil lionUtil;

    @Mock
    private TopicResourceService topicResourceService;

    private TopicCfgLionHandler lionEditor;

    private LionEditorEntity lionFilterEntity;

    private EmptyObject result;

    @SuppressWarnings("unchecked")
	private LionHandlerChain lionFilterChain = new LionHandlerChain();

    @Before
    public void setUp() throws Exception {

        TopicCfgLionHandler topicCfgLionFilter = new TopicCfgLionHandler();

        lionFilterEntity = new LionEditorEntity();
        lionFilterEntity.setTest(Boolean.TRUE);
        lionFilterEntity.setTopic("swallow-qa-test");

        lionFilterEntity.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
        lionFilterEntity.setStorageServer(MongoServerHandler.PRE_MONGO + "11.22.33.44:8000,55.66.77.88:8001");
        lionFilterEntity.setSize4SevenDay(500);

        lionEditor = topicCfgLionFilter;
        lionEditor.setLionUtil(lionUtil);
        lionEditor.setTopicResourceService(topicResourceService);

        String topic = lionFilterEntity.getTopic();
        String key = "swallow.topiccfg." + topic;
        MongoConfigBean mongoConfigBean = new MongoConfigBean();
        String mongoURL = lionFilterEntity.getStorageServer();
        mongoConfigBean.setMongoUrl(mongoURL);
        mongoConfigBean.setSize(lionFilterEntity.getSize4SevenDay());

        JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
        String value = jsonBinder.toJson(mongoConfigBean);
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

        lionFilterChain.addHandler(lionEditor);
    }

    @Test
    public void test() {

        result = new EmptyObject();
        ResponseStatus status = lionFilterChain.handle(lionFilterEntity, result);
        Assert.assertTrue(status.getStatus() == 0);

    }

    @Test
    public void testQa(){
        lionFilterEntity.setEnv(TopicApplyController.QA);
        lionFilterEntity.setTopicType(TOPIC_TYPE.DURABLE_FIRST.toString());
        lionFilterEntity.setSize4SevenDay(-1);
        lionFilterEntity.setStorageServer(KafkaServerHandler.PRE_KAFKA + "11.22.33.44:8000,55.66.77.88:8001");
        lionFilterEntity.setTest(false);
        result = new EmptyObject();
        ResponseStatus status = lionFilterChain.handle(lionFilterEntity, result);
        Assert.assertTrue(status.getStatus() == 0);
    }
}