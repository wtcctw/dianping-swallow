package com.dianping.swallow.web.model.dom;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午5:13:05
 */
public class MongoConfigBean extends ServerConfigBean{

	public String getMongoUrl() {
		return mongoUrl;
	}

	public void setMongoUrl(String mongoUrl) {
		this.mongoUrl = mongoUrl;
	}

	private String mongoUrl;

}
