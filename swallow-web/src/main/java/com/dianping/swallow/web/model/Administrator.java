package com.dianping.swallow.web.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:05:51
 */
public class Administrator {
	@Id
	private String id;

	@Indexed
	private String name;

	private UserType role;

	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public Administrator setName(String name) {
		this.name = name;
		return this;
	}

	public UserType getRole() {
		return role;
	}

	public Administrator setRole(UserType role) {
		this.role = role;
		return this;
	}

	public String getId() {
		return id;
	}

	public Administrator setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public String toString() {
		return "Administrator [id=" + id + ", name=" + name + ", role=" + role + ", date=" + date + "]";
	}

}
