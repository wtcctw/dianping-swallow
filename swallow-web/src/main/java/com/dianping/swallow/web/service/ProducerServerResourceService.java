package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ProducerServerResource;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午4:46:32
 */
public interface ProducerServerResourceService extends ServerResourceService {

	boolean insert(ProducerServerResource producerServerResource);

	boolean update(ProducerServerResource producerServerResource);

	int remove(String ip);

	List<ProducerServerResource> findAll();

	Pair<Long, List<ProducerServerResource>> findProducerServerResourcePage(int offset, int limit);

	ProducerServerResource buildProducerServerResource(String ip, String hostName);
}
