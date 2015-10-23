package com.dianping.swallow.web.model.server;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午6:09:09
 */
public interface Sendable {

	void checkSender(long sendTimeStamp);

	String senderIp();

}
