package com.dianping.swallow.web.controller.handler.data;

import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import org.apache.commons.lang.StringUtils;

/**
 * @author mingdongli
 *         15/10/23 下午4:18
 */
public class LionEditorEntity extends LionConfigureResult{

    private String topic;

    private boolean test;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isKafkaType(){
        String topicType = getTopicType();
        int size = getSize4SevenDay();
        if(size < 0 && StringUtils.isNotBlank(topicType)){
            return true;
        }
        return false;
    }
}
