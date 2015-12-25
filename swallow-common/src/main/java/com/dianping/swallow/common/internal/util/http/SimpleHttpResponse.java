package com.dianping.swallow.common.internal.util.http;

import org.apache.http.StatusLine;

/**
 * @author mengwenchao
 *
 * 2015年12月25日 下午2:42:03
 */
public class SimpleHttpResponse<T> {
	
	private StatusLine statusLine;
	
	private T content;
	
	public SimpleHttpResponse(T content, StatusLine statusLine){
		
		this.content = content;
		this.statusLine = statusLine;
	}
	
	public int getStatusCode() {
		return statusLine.getStatusCode();
	}
	
	public T getContent() {
		return content;
	}

	@Override
	public String toString() {
		return getStatusCode() + "," + content;
	}

}
