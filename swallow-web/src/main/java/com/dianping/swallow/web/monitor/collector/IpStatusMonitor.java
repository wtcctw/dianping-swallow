package com.dianping.swallow.web.monitor.collector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年10月8日 上午11:06:00
 */
public class IpStatusContainer<T> {

	private static final long ACTIVE_TIMESPAN = 6 * 60 * 60 * 1000;

	private Map<T, ActiveIpData> resouceActiveIpDatas = new ConcurrentHashMap<T, ActiveIpData>();

	public static class ActiveIpData {

		private Map<String, Long> activeDatas = new ConcurrentHashMap<String, Long>();

		public void addData(String ip, boolean hasData) {
			if (hasData) {
				activeDatas.put(ip, System.currentTimeMillis());
			} else {
				if (!activeDatas.containsKey(ip)) {
					activeDatas.put(ip, System.currentTimeMillis());
				}
			}
		}

		public boolean isIpInActive(String ip) {
			if (activeDatas.containsKey(ip)) {
				return System.currentTimeMillis() - activeDatas.get(ip) > ACTIVE_TIMESPAN;
			}
			return true;
		}
	}

	public Map<T, ActiveIpData> getResouceActiveIpDatas() {
		return resouceActiveIpDatas;
	}

	public void setResouceActiveIpDatas(Map<T, ActiveIpData> resouceActiveIpDatas) {
		this.resouceActiveIpDatas = resouceActiveIpDatas;
	}

	public void putActiveIpData(T key, String activeIp, boolean hasData) {
		ActiveIpData activeIpData = null;
		if (resouceActiveIpDatas.containsKey(key)) {
			activeIpData = resouceActiveIpDatas.get(key);
		} else {
			activeIpData = new ActiveIpData();
			resouceActiveIpDatas.put(key, activeIpData);
		}
		activeIpData.addData(activeIp, hasData);
	}

	public void putActiveIpData(T key, ActiveIpData activeIpData) {
		resouceActiveIpDatas.put(key, activeIpData);
	}

	public ActiveIpData getActiveIpData(T key) {
		if (resouceActiveIpDatas.containsKey(key)) {
			return resouceActiveIpDatas.get(key);
		}
		return null;
	}

	public Set<String> getInActiveIps(T key) {
		Set<String> activeIps = new HashSet<String>();
		if (resouceActiveIpDatas.containsKey(key)) {
			ActiveIpData activeIpData = resouceActiveIpDatas.get(key);
			for (String ip : activeIpData.activeDatas.keySet()) {
				if (StringUtils.isNotBlank(ip)) {
					if (activeIpData.isIpInActive(ip)) {
						activeIps.add(ip);
					}
				}
			}
		}
		return activeIps;
	}

	public void removeActiveIp(T key, String ip) {
		if (resouceActiveIpDatas.containsKey(key)) {
			ActiveIpData activeIpData = resouceActiveIpDatas.get(key);
			if (activeIpData.activeDatas.containsKey(ip)) {
				activeIpData.activeDatas.remove(ip);
			}
		}
	}

}
