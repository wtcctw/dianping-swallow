package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午3:31:50
 */
public enum ServerType {

	MASTER("master"),
	SLAVE("slave");
	
	private String type;
	
	private ServerType(){
	}
	
	private ServerType(String type){
		this.type = type;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static ServerType findByType(String type) {
		for (ServerType serverType : values()) {
			if (type.equals(serverType.getType())) {
				return serverType;
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}
	
	@Override
	public String toString() {
		return type;
	}
}
