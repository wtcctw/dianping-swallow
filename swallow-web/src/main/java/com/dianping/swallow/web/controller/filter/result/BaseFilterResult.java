package com.dianping.swallow.web.controller.filter.result;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:23:49
 */
public class BaseFilterResult {
	
	public String message = "success";
	
	public int status = 0;
	
	public BaseFilterResult(){
		
	}

	public BaseFilterResult(String message, int status){
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

}
