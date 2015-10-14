package com.dianping.swallow.web.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AppResourceManager;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ApplicationResourceService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.service.TopicResourceService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午9:42:45
 */
@Service("appResourceManager")
public class AppResourceManagerImpl implements AppResourceManager {

	@Autowired
	private IpResourceService ipResourceService;

	@Autowired
	private ApplicationResourceService appResourceService;

	@Autowired
	private ConsumerIdResourceService cResourceService;

	@Autowired
	private TopicResourceService topicResourceService;

	@Override
	public List<ApplicationResource> getAppResourceByPTopic(String topicName) {
		TopicResource topicResource = topicResourceService.findByTopic(topicName);

		if (topicResource == null) {
			return null;
		}
		List<String> pApplications = topicResource.getProducerApplications();
		if (pApplications != null && !pApplications.isEmpty()) {
			return getAppResourceByName(pApplications);
		}
		List<IpInfo> pIpInfos = topicResource.getProducerIpInfos();
		List<String> alarmIps = getValidIps(pIpInfos);
		return getAppResourceByIp(alarmIps);
	}

	@Override
	public List<ApplicationResource> getAppResourceByConsumerId(String topicName, String consumerId) {
		ConsumerIdResource consumerIdResource = cResourceService.findByConsumerIdAndTopic(topicName, consumerId);

		if (consumerIdResource == null) {
			return null;
		}

		List<String> cApplications = consumerIdResource.getConsumerApplications();
		if (cApplications != null && !cApplications.isEmpty()) {
			return getAppResourceByName(cApplications);
		}

		List<IpInfo> pIpInfos = consumerIdResource.getConsumerIpInfos();
		List<String> alarmIps = getValidIps(pIpInfos);
		return getAppResourceByIp(alarmIps);
	}

	@Override
	public List<ApplicationResource> getAppResourceByIp(String ip) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		List<IpResource> ipResources = ipResourceService.findByIp(ip);
		if (ipResources != null && !ipResources.isEmpty()) {
			List<String> appNames = new ArrayList<String>();
			for (IpResource ipResource : ipResources) {
				if (StringUtils.isNotBlank(ipResource.getApplication())) {
					appNames.add(ipResource.getApplication());
				}
			}
			return getAppResourceByName(appNames);
		}
		return null;
	}

	@Override
	public List<ApplicationResource> getAppResourceByIp(List<String> ips) {
		if (ips == null || ips.isEmpty()) {
			return null;
		}
		String[] strIps = ips.toArray(new String[ips.size()]);
		List<IpResource> ipResources = ipResourceService.findByIps(strIps);
		if (ipResources != null && !ipResources.isEmpty()) {
			List<String> appNames = new ArrayList<String>();
			for (IpResource ipResource : ipResources) {
				if (StringUtils.isNotBlank(ipResource.getApplication())) {
					appNames.add(ipResource.getApplication());
				}
			}
			return getAppResourceByName(appNames);
		}
		return null;
	}

	@Override
	public List<ApplicationResource> getAppResourceByName(String appName) {
		return appResourceService.findByApplication(appName);
	}

	@Override
	public List<ApplicationResource> getAppResourceByName(List<String> appNames) {
		if (appNames != null && !appNames.isEmpty()) {
			String[] strAppNameArr = appNames.toArray(new String[appNames.size()]);
			return appResourceService.findByApplication(strAppNameArr);
		}
		return null;
	}

	private List<String> getValidIps(List<IpInfo> ipInfos) {
		if (ipInfos != null && !ipInfos.isEmpty()) {
			List<String> alarmIps = new ArrayList<String>();
			for (IpInfo ipInfo : ipInfos) {
				if (ipInfo.isActive()) {
					alarmIps.add(ipInfo.getIp());
				}
			}
			return alarmIps;
		}
		return null;
	}
}
