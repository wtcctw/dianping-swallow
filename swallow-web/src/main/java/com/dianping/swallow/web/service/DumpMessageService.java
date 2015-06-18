package com.dianping.swallow.web.service;


/**
 * @author mingdongli
 *
 * 2015年6月16日下午5:34:31
 */

public interface DumpMessageService extends SwallowService {
	
	/**
	 * 
	 * @param topicName  topic名称
	 * @param startdt    开始时间
	 * @param stopdt     结束时间
	 * @param filename   保存文件名
	 */
	Integer exportMessageByTimeSpan(String topicName, String startdt, String stopdt, String filename);

}
