package com.dianping.swallow.web.model.dom;

/**
 * Author   mingdongli
 * 16/3/1  上午10:55.
 */
public class KafkaConfigBean extends ServerConfigBean{

    private String storeUrl;

    private String topicType;

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }
}
