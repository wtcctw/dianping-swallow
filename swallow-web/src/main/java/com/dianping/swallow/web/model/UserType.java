package com.dianping.swallow.web.model;


/**
 * @author mingdongli
 *
 * 2015年8月19日上午11:48:52
 */
public enum UserType {
	
	ROOT("Root"),
	ADMINISTRATOR("Administrator"),
	USER("User"),
	VISITOR("Visitor");
	
	private String type;
	
	private UserType(){
	}
	
	private UserType(String type){
		this.type = type;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static UserType findByType(String type) {
		for (UserType userType : values()) {
			if (type.equals(userType.getType())) {
				return userType;
			}
		}
		throw new RuntimeException("Error typy : " + type);
	}
	
	@Override
	public String toString() {
		return type;
	}

}
