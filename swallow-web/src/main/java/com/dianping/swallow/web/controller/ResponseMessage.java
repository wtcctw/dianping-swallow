package com.dianping.swallow.web.controller;

public class ResponseMessage {
	
	private int code;
	/**
	 * 0 成功
	 * 正数 失败重试可以成功  1 network 2 ...
	 * 负数 失败重试不能成功  -1 topicNameWrong -2 ....
	 */
	private String message;

	
}
