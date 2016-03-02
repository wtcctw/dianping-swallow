package com.dianping.swallow.web.model.dom;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午5:13:05
 */
public class MongoConfigBean extends ServerConfigBean{

	private String mongoUrl;

	private int size;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getMongoUrl() {
		return mongoUrl;
	}

	public void setMongoUrl(String mongoUrl) {
		this.mongoUrl = mongoUrl;
	}


}
