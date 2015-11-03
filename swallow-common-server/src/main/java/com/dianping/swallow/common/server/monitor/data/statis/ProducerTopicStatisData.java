package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午3:14:45
 */
public class ProducerTopicStatisData extends AbstractTotalMapStatisable<MessageInfo, ProducerTopicData>{

	@Override
	protected Class<? extends Statisable<MessageInfo>> getStatisClass() {
		return MessageInfoStatis.class;
	}

}
