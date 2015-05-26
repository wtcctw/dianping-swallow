package com.dianping.swallow.common.server.monitor.data.structure;





/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public class ProducerServerData extends TotalMap<ProducerTopicData>{

	private static final long serialVersionUID = 1L;

	@Override
	protected ProducerTopicData createValue() {
		return new ProducerTopicData();
	}

}
