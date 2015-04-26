package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;



/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public class MessageInfoTotalMap extends TotalMap<MessageInfo>{

	private static final long serialVersionUID = 1L;

	@Override
	protected  MessageInfo createValue() {
		return new MessageInfo();
	}

}
