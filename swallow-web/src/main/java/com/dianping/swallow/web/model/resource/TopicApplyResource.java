package com.dianping.swallow.web.model.resource;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.util.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author   mingdongli
 * 15/11/20  上午11:21.
 */
@Document(collection = "TOPIC_APPLY_RESOURCE")
public class TopicApplyResource extends BaseResource {

    @Indexed(name = "IX_TOPIC", direction = IndexDirection.ASCENDING, unique = true, dropDups = true)
    private String topic;

    public TopicApplyDto topicApplyDto;

    public LionConfigureResult lionConfigureResult;

    public ResponseStatus responseStatus;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public TopicApplyDto getTopicApplyDto() {
        return topicApplyDto;
    }

    public void setTopicApplyDto(TopicApplyDto topicApplyDto) {
        this.topicApplyDto = topicApplyDto;
    }

    public LionConfigureResult getLionConfigureResult() {
        return lionConfigureResult;
    }

    public void setLionConfigureResult(LionConfigureResult lionConfigureResult) {
        this.lionConfigureResult = lionConfigureResult;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    @JsonIgnore
    public boolean isDefault() {
        if (DEFAULT_RECORD.equals(topicApplyDto.getTopic())) {
            return true;
        }
        return false;
    }
}
