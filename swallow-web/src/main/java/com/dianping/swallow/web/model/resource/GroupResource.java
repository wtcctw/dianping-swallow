package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author   mingdongli
 * 15/12/22  下午3:42.
 */
@Document(collection = "GROUP_RESOURCE")
public class GroupResource extends BaseResource{

    @Indexed(name = "IX_GROUP", direction = IndexDirection.ASCENDING, unique = true, dropDups = true)
    private String groupName;

    private String desc;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean isDefault() {
        if (DEFAULT_RECORD.equals(groupName)) {
            return true;
        }
        return false;
    }
}
