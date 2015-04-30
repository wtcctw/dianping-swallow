package com.dianping.swallow.common.server.monitor.collector;

import com.dianping.swallow.common.internal.action.AbstractSwallowAction;

/**
 * @author mengwenchao
 *
 * 2015年4月30日 下午2:46:25
 */
public abstract class AbstractMonitorDataAction extends AbstractSwallowAction{
	
	protected String serverIp;
	protected String topicInfo;
	protected Long 	 messageId;

	public AbstractMonitorDataAction(String topicInfo, String serverIp, Long messageId){
		
		this(topicInfo, serverIp);
		this.messageId = messageId;
	}

	public AbstractMonitorDataAction(String topicInfo, String serverIp){
		this.serverIp = serverIp;
		this.topicInfo = topicInfo;
	}
	
	@Override
	public String toString() {
		
		String result = "";
		
		if(serverIp != null){
			result += "serverIp:" + serverIp; 
		}
		if(topicInfo != null){
			result += ", topic:" + topicInfo;
		}
		if(messageId != null){
			result += ", messageId:" + messageId;
		}
		return result;  
	}

}
