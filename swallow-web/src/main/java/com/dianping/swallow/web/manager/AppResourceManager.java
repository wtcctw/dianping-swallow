package com.dianping.swallow.web.manager;

import java.util.List;

import com.dianping.swallow.web.model.resource.ApplicationResource;

/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午9:25:17
 */
public interface AppResourceManager {

	List<ApplicationResource> getAppResourceByPTopic(String topicName);

	List<ApplicationResource> getAppResourceByConsumerId(String topicName, String consumerId);

	List<ApplicationResource> getAppResourceByIp(String ip);

	List<ApplicationResource> getAppResourceByIp(List<String> ips);

	List<ApplicationResource> getAppResourceByName(String appName);

	List<ApplicationResource> getAppResourceByName(List<String> appNames);

}
