package com.dianping.swallow.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:05:51
 */
public class Administrator {
	@Id
	private String id;

	@Indexed
	private String name;
	
	//0:admin, 3:common
	private int    role;
	
	private String date;
	
	public Administrator(){
		
	}
	
	public Administrator(String name,int role, String date) {
		this.name = name;
		this.role = role;
		this.date = date;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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
		return "Administrator [id=" + id + ", name=" + name + ", role=" + role + ", date=" + date + "]";
	}

}
