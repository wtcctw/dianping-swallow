package com.dianping.swallow.controller;

/**
 * @author mengwenchao
 *
 * 2015年4月15日 下午10:48:23
 */
public class People {
	
	private String name;

	public People(){
		
	}
	
	public People(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public String toString() {
		return name;
	}
}
