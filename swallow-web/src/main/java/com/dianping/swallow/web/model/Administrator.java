package com.dianping.swallow.web.model;

import org.springframework.data.annotation.Id;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:51
 */
public class Administrator {
	@Id
	private String id;

	private String name; //chinese
	
	private String email;  //email

	//0:admin, 3:common
	private int    role;
	
	private String date;

	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}

	public Administrator(String name, String email,int role, String date) {
		this.name = name;
		this.email = email;
		this.role = role;
		this.date = date;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public int getRole() {
		return role;
	}


	public void setRole(int role) {
		this.role = role;
	}


	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Administrator [id=" + id + ", name=" + name + ", email="
				+ email + ", role=" + role + ", date=" + date + "]";
	}

}
