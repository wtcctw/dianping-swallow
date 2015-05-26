package com.dianping.swallow.common.server.monitor.data.structure;





/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public class ConsumerServerData extends TotalMap<ConsumerTopicData>{

	private static final long serialVersionUID = 1L;

	@Override
	protected  ConsumerTopicData createValue() {
		return new ConsumerTopicData();
	}

}
