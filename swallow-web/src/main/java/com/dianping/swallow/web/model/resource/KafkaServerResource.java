package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author   mingdongli
 * 16/1/28  下午2:49.
 */
@Document(collection = "KAFKA_RESOURCE")
public class KafkaServerResource extends ServerResource{

    private int port;

    private String zkServers;

    private int groupId;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }
}
