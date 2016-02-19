package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author   mingdongli
 * 16/2/18  下午5:45.
 */
@Document(collection = "JMX_RESOURCE")
public class JmxResource extends BaseResource {

    private String group;

    @Indexed(name = "IX_NAME", direction = IndexDirection.ASCENDING)
    private String name;

    private String type;

    private String tag;

    private String clazz;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

}