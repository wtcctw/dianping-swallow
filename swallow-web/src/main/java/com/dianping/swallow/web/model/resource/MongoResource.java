package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author mingdongli
 *         <p/>
 *         2015年9月17日下午7:52:52
 */
@Document(collection = "MONGO_RESOURCE")
public class MongoResource extends ServerResource {

    @Indexed(name = "IX_CATALOG", direction = IndexDirection.ASCENDING)
    private String catalog;

    private Float load;

    private Float disk;

    private Integer qps;

    public Float getLoad() {
        return load;
    }

    public void setLoad(Float load) {
        this.load = load;
    }

    public Float getDisk() {
        return disk;
    }

    public void setDisk(Float disk) {
        this.disk = disk;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Integer getQps() {
        return qps;
    }

    public void setQps(Integer qps) {
        this.qps = qps;
    }

    @Override
    public boolean isDefault() {
        return super.isDefault();
    }

}
