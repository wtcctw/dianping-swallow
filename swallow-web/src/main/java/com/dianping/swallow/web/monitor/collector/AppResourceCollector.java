package com.dianping.swallow.web.monitor.collector;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ApplicationResourceService;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPDescService;
import com.dianping.swallow.web.service.IpResourceService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 下午4:02:01
 */
@Component
public class AppResourceCollector extends AbstractResourceCollector {

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private IpResourceService ipResourceService;

	@Autowired
	private ApplicationResourceService appResourceService;

	@Autowired
	private IPDescService ipDescService;

	@Autowired
	private CmdbService cmdbService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		collectorInterval = 60;
		collectorDelay = 2;
	}

	@Override
	public void doCollector() {
		logger.info("[doCollector] start collect appResource.");
		doIpResource();
	}

	@Override
	public int getCollectorDelay() {
		return collectorDelay;
	}

	public void doIpResource() {
		Set<String> producerIps = producerStatsDataWapper.getIps(false);
		Set<String> consuemerIps = consumerStatsDataWapper.getIps(false);
		doAppResourceTask(producerIps);
		doAppResourceTask(consuemerIps);
	}

	private void doAppResourceTask(Set<String> ips) {
		for (String ip : ips) {
			if (StringUtils.isBlank(ip)) {
				continue;
			}
			try {
				IPDesc ipDesc = cmdbService.getIpDesc(ip);
				if (ipDesc != null && StringUtils.isNotBlank(ipDesc.getName())) {
					IpResource ipResourceInDb = ipResourceService.findByIp(ip, ipDesc.getName());
					if (ipResourceInDb == null) {
						ipDescService.addEmail(ipDesc);
						// AppResource
						ApplicationResource appResource = new ApplicationResource();
						appResource.buildApplicationResource(ipDesc);
						appResourceService.update(appResource);
						// IpResource
						IpResource ipResource = new IpResource(ipDesc.getIp(), ipDesc.getName(), true);
						ipResource.setCreateTime(new Date());
						ipResource.setUpdateTime(new Date());
						ipResourceService.insert(ipResource);
					} else {
						ipDescService.addEmail(ipDesc);
						// AppResource
						ApplicationResource appResource = new ApplicationResource();
						appResource.buildApplicationResource(ipDesc);
						appResourceService.update(appResource);
						// IpResource
						ipResourceInDb.setUpdateTime(new Date());
						ipResourceService.update(ipResourceInDb);
					}
				} else {
					List<IpResource> ipResourceInDbs = ipResourceService.findByIp(ip);
					IpResource ipResource = null;
					if (ipResourceInDbs == null || ipResourceInDbs.isEmpty()) {
						ipResource = new IpResource(ip, StringUtils.EMPTY, false);
						ipResource.setCreateTime(new Date());
						ipResource.setUpdateTime(new Date());
						ipResourceService.insert(ipResource);
					}
				}
			} catch (Exception e) {
				logger.error("[doIpResourceTask] update ip resouces error.{}", ip, e);
			}
		}
	}

	@Override
	public int getCollectorInterval() {
		return collectorInterval;
	}

	public void setCollectorInterval(int collectorInterval) {
		this.collectorInterval = collectorInterval;
	}

}
