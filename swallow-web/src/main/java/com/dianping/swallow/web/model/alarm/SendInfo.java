package com.dianping.swallow.web.model.alarm;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 
 * @author qiyin
 *
 * 2015年8月6日 下午4:47:53
 */
public class SendInfo {

	@Indexed(name ="IX_CREATETIME")
	private String receiver;

	private SendType sendType;

	private ResultType resultType;

	public String getReceiver() {
		return receiver;
	}

	public SendInfo setReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	public SendType getSendType() {
		return sendType;
	}

	public SendInfo setSendType(SendType sendType) {
		this.sendType = sendType;
		return this;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public SendInfo setResultType(ResultType resultType) {
		this.resultType = resultType;
		return this;
	}
	
}
