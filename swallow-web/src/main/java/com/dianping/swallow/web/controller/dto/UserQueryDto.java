package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月19日上午11:43:32
 */
public class UserQueryDto extends BaseDto{
	
	private String name;
	
	private String role; 
	
	public UserQueryDto(){
		
	}
	
	public UserQueryDto(String name, String role){
		this.name = name;
		this.role = role;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}
