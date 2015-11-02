package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.filter.config.LionConfigManager;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author mingdongli
 *         15/10/23 下午8:19
 */
public class ConsumerServerLionHandlerTest extends MockTest {

    @Mock
    private LionUtil lionUtil;

    @Mock
    private TopicResourceService topicResourceService;

    @Mock
    private LionConfigManager lionConfigManager;

    @Mock
    private ConsumerServerResourceServiceImpl consumerServerResourceService;

    private ConsumerServerLionHandler lionEditor;

    private LionEditorEntity lionFilterEntity;

    private EmptyObject result;

    private LionHandlerChain lionHandlerChain = new LionHandlerChain();

    @Before
    public void setUp() throws Exception {

        ConsumerServerLionHandler consumerServerLionEditor = new ConsumerServerLionHandler();

        lionFilterEntity = new LionEditorEntity();
        lionFilterEntity.setTest(Boolean.TRUE);
        lionFilterEntity.setTopic("swallow-test");

        lionFilterEntity.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
        lionFilterEntity.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
        lionFilterEntity.setSize4SevenDay(500);

        lionEditor = consumerServerLionEditor;
        lionEditor.setLionUtil(lionUtil);
        lionEditor.setTopicResourceService(topicResourceService);
        lionEditor.setLionConfigManager(lionConfigManager);

        String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
        Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();
        int length = 10;
        Mockito.doReturn(length).when(lionConfigManager).getConsumerServerUriLength();

        String key = "swallow.consumer.consumerServerURI";
        Mockito.doReturn(consumerServerConfig).when(lionUtil).getValue(key);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(consumerServerConfig).append(";\n").append(lionFilterEntity.getTopic()).append("=")
                .append(lionFilterEntity.getConsumerServer());
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());

        lionHandlerChain.addHandler(lionEditor);
    }

    @Test
    public void test() {

        result = new EmptyObject();
        ResponseStatus status = lionHandlerChain.handle(lionFilterEntity, result);
        Assert.assertTrue(status.getStatus() == 0);

    }

}