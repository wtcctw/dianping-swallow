package com.dianping.swallow.common.internal.config;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年10月30日 下午6:53:15
 */
public class TopicConfig implements Cloneable {

    private String storeUrl;

    private Integer size;

    private Integer max;

    private TOPIC_TYPE topicType = TOPIC_TYPE.DURABLE_FIRST;

    private String group;

    public TopicConfig() {

    }

    public TopicConfig(String storeUrl, int size, int max) {

        this(storeUrl, size, max, TOPIC_TYPE.DURABLE_FIRST);
    }

    public TopicConfig(String storeUrl, int size, int max, TOPIC_TYPE topicType) {

        this.storeUrl = StringUtils.trimToNull(storeUrl);
        this.size = size;
        this.max = max;
        this.topicType = topicType;

    }


    /**
     * 所有选项皆有效
     *
     * @return
     */
    public boolean allValid() {

        return !StringUtils.isEmpty(storeUrl)
                && (size != null && size > 0)
                && (max != null && max > 0)
                && topicType != null
                && !StringUtils.isEmpty(group);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        TopicConfig topicCfg = (TopicConfig) super.clone();

        return topicCfg;
    }

    public String toJson() {
        return JsonBinder.getNonEmptyBinder().toJson(this);

    }

    public static TopicConfig fromJson(String jsonString) {

        return JsonBinder.getNonEmptyBinder().fromJson(jsonString, TopicConfig.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TopicConfig)) {
            return false;
        }

        TopicConfig cmp = (TopicConfig) obj;

        return (storeUrl == null ? cmp.storeUrl == null : storeUrl.equals(cmp.storeUrl))
                && (size == null ? cmp.size == null : size.equals(cmp.size))
                && (max == null ? cmp.max == null : max.equals(cmp.max))
                && (topicType == null ? cmp.topicType == null : topicType.equals(cmp.topicType))
                && (group == null ? cmp.group == null : group.equals(cmp.group));

    }


    public void setSize(Integer size) {
        this.size = size;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Deprecated
    public void setMongoUrl(String mongoUrl) {
        this.storeUrl = mongoUrl;
    }

    public void setStoreUrl(String storeUrl) {

        this.storeUrl = storeUrl;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getMax() {
        return max;
    }

    public TOPIC_TYPE getTopicType() {
        return topicType;
    }

    public void setTopicType(TOPIC_TYPE topicType) {
        this.topicType = topicType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
