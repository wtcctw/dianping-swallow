package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.model.resource.ProducerServerResource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午4:21:57
 */
public interface ProducerServerResourceDao extends ServerResourceDao{

	boolean insert(ProducerServerResource producerServerResource);

	boolean update(ProducerServerResource producerServerResource);

	int remove(String ip);
	
	long count();

	ProducerServerResource findByIp(String ip);

	ProducerServerResource findByHostname(String hostname);

	ProducerServerResource findDefault();
	
	Pair<Long, List<ProducerServerResource>> findProducerServerResourcePage(BaseDto baseDto);

}
