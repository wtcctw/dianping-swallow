package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.server.monitor.data.ProducerData;




/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public class ProducerTotalMap extends TotalMap<ProducerData>{

	private static final long serialVersionUID = 1L;

	@Override
	protected ProducerData createValue() {
		return new ProducerData();
	}

}
