package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfoTotalMap;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 下午2:00:08
 */
public class MessageInfoTotalMapStatis extends AbstractTotalMapStatisable<MessageInfo, MessageInfoTotalMap>{

	@Override
	protected Class<? extends Statisable<MessageInfo>> getStatisClass() {
		
		return MessageInfoStatis.class;
	}

}
