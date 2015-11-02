package com.dianping.swallow.web.model.resource;

/**
 * @author mingdongli
 *
 * 2015年9月21日上午11:32:33
 */
public enum MongoType {
	
	SEARCH("搜索消息队列"),
	PAYMENT("下单消息队列"),
	GENERAL("一般消息队列");
	
	private String type;
	
	private MongoType(){
	}
	
	private MongoType(String type){
		this.type = type;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static MongoType findByType(String type) {
		for (MongoType mongoType : values()) {
			if (type.startsWith(mongoType.getType())) {
				return mongoType;
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}
	
	@Override
	public String toString() {
		return type;
	}
	
}
