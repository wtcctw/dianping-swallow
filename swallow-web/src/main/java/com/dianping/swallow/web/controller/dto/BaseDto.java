package com.dianping.swallow.web.controller.dto;

public class BaseDto {
	
	private int offset;
	
	private int limit;
	
	public BaseDto(){
		
	}
	
	public BaseDto(int offset, int limit){
		this.offset = offset;
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
