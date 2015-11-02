package com.dianping.swallow.web.controller.handler.data;

import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;

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
}
