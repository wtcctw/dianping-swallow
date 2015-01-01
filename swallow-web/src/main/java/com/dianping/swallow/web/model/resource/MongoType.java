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
	
	MongoType(String type){
		this.type = type;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static String StringToType(String type) {
		for (MongoType mongoType : values()) {
			if (type.startsWith(mongoType.getType())) {
				return mongoType.toString();
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}
	
	public static String findString(String type) {
		for (MongoType mongoType : values()) {
			if (type.startsWith(mongoType.getType())) {
				return type;
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}

	public static MongoType findType(String type) {
		for (MongoType mongoType : values()) {
			if (type.startsWith(mongoType.toString())) {
				return mongoType;
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}
	
	public static void main(String[] args) {
		System.out.println(findString("一般消息队列"));
		System.out.println(findType(MongoType.GENERAL.toString()));
		System.out.println(StringToType("一般消息队列"));
	}
	
}
