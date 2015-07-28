package com.dianping.swallow.web.util;

/**
 * @author mingdongli
 *
 *         2015年6月12日下午5:44:19
 */
public enum ResponseStatus {

	INVALIDTOPIC("no such topic", -10), TOPICBLANK("topic blank", -9), IOEXCEPTION("io exception", -8), RUNTIMEEXCEPTION(
			"runtime exception", -7), INTERRUPTEDEXCEPTION("interrupted exception", -6), PARSEEXCEPTION("parse error",
			-5), EMPTYCONTENT("empty content", -4), NOAUTHENTICATION("no authenticaton", -3), UNAUTHENTICATION(
			"unauthorized", -2), MONGOWRITE("write mongo error", -1), SUCCESS("success", 0), TRY_MONGOWRITE(
			"read time out", 1);

	private String message;

	private int status;

	private ResponseStatus(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public ResponseStatus setMessage(String message) {
		this.message = message;
		return this;
	}

	public int getStatus() {
		return status;
	}

	public ResponseStatus setStatus(int status) {
		this.status = status;
		return this;
	}

	public static String findByStatus(int status) {
		for (ResponseStatus code : values()) {
			if (status == code.getStatus()) {
				return code.getMessage();
			}
		}
		throw new RuntimeException("Error status : " + status);
	}

	@Override
	public String toString() {
		return this.message + "_" + this.status;
	}

}
