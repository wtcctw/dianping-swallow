package com.dianping.swallow.web.manager.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmReceiverManager;
import com.dianping.swallow.web.manager.AppResourceManager;
import com.dianping.swallow.web.model.resource.ApplicationResource;

/**
 * 
 * @author qiyin
 *
 */
@Service("alarmReceiverManager")
public class AlarmReceiverManagerImpl implements AlarmReceiverManager {

	private static final String COMMA_SPLIT = ",";

	private static final String SWALLOW_APP_NAME = "swallow-producer";

	@Autowired
	private AppResourceManager appResourceManager;

	@Override
	public AlarmReceiver getSwallowReceiver() {

		return getAlarmReceiverByName(SWALLOW_APP_NAME);
	}

	@Override
	public AlarmReceiver getAlarmReceiverByName(String appName) {
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByName(appName);
		return getAlarmReceiverByApp(appResources);
	}

	private AlarmReceiver getAlarmReceiverByApp(List<ApplicationResource> appResources) {
		if (appResources != null && !appResources.isEmpty()) {
			AlarmReceiver alarmReceiver = new AlarmReceiver();
			for (ApplicationResource appResource : appResources) {
				addElement(alarmReceiver.getEmails(), appResource.getEmail());
				addElement(alarmReceiver.getEmails(), appResource.getOpEmail());
				addElement(alarmReceiver.getMobiles(), appResource.getDpMobile());
				addElement(alarmReceiver.getMobiles(), appResource.getOpMobile());
			}
			return alarmReceiver;
		}
		return null;
	}

	@Override
	public AlarmReceiver getAlarmReceiverByName(List<String> appNames) {
		if (appNames == null) {
			return null;
		}
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByName(appNames);
		return getAlarmReceiverByApp(appResources);
	}

	@Override
	public AlarmReceiver getAlarmReceiverByIp(String ip) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByIp(ip);
		return getAlarmReceiverByApp(appResources);
	}

	@Override
	public AlarmReceiver getAlarmReceiverByIp(List<String> ips) {
		if (ips == null || ips.isEmpty()) {
			return null;
		}
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByIp(ips);
		return getAlarmReceiverByApp(appResources);
	}

	@Override
	public AlarmReceiver getAlarmReceiverByPTopic(String topicName) {
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByPTopic(topicName);
		return getAlarmReceiverByApp(appResources);
	}

	@Override
	public AlarmReceiver getAlarmReceiverByConsumerId(String topicName, String consumerId) {
		List<ApplicationResource> appResources = appResourceManager.getAppResourceByConsumerId(topicName, consumerId);
		return getAlarmReceiverByApp(appResources);
	}

	private void addElement(Collection<String> elementSet, String strElement) {
		if (StringUtils.isBlank(strElement)) {
			return;
		}
		String[] elements = strElement.split(COMMA_SPLIT);
		if (elements != null) {
			for (String element : elements) {
				if (StringUtils.isNotBlank(element)) {
					elementSet.add(element);
				}
			}
		}
	}

}
