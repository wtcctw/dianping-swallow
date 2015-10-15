package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.resource.IpInfo;

/**
 * 
 * @author qiyin
 *
 *         2015年10月8日 上午11:06:00
 */
public class IpStatusMonitor<T> {

	private static final long ACTIVE_TIMESPAN = 24 * 60 * 60 * 1000;

	private Map<T, ActiveIpData> resourceActiveIpDatas = new ConcurrentHashMap<T, ActiveIpData>();

	private final long startTimestamp = System.currentTimeMillis();

	public static class ActiveIpData {

		private Map<String, Long> activeDatas = new ConcurrentHashMap<String, Long>();

		public void addData(String ip, boolean hasData) {
			if (hasData) {
				activeDatas.put(ip, System.currentTimeMillis());
			}
		}

		public boolean isIpInActive(String ip) {
			if (activeDatas.containsKey(ip)) {
				return System.currentTimeMillis() - activeDatas.get(ip) > ACTIVE_TIMESPAN;
			}
			return true;
		}
	}

	public boolean isValid() {
		return System.currentTimeMillis() - startTimestamp > ACTIVE_TIMESPAN;
	}

	public Map<T, ActiveIpData> getResourceActiveIpDatas() {
		return resourceActiveIpDatas;
	}

	public void setResourceActiveIpDatas(Map<T, ActiveIpData> resourceActiveIpDatas) {
		this.resourceActiveIpDatas = resourceActiveIpDatas;
	}

	public void putActiveIpData(T key, String activeIp, boolean hasData) {
		ActiveIpData activeIpData = null;
		if (resourceActiveIpDatas.containsKey(key)) {
			activeIpData = resourceActiveIpDatas.get(key);
		} else {
			activeIpData = new ActiveIpData();
			resourceActiveIpDatas.put(key, activeIpData);
		}
		activeIpData.addData(activeIp, hasData);
	}

	public void putActiveIpData(T key, ActiveIpData activeIpData) {
		resourceActiveIpDatas.put(key, activeIpData);
	}

	public ActiveIpData getActiveIpData(T key) {
		if (resourceActiveIpDatas.containsKey(key)) {
			return resourceActiveIpDatas.get(key);
		}
		return null;
	}

	public Set<String> getInActiveIps(T key, List<IpInfo> ipInfos) {
		Set<String> inActiveIps = new HashSet<String>();
		if (resourceActiveIpDatas.containsKey(key)) {
			ActiveIpData activeIpData = resourceActiveIpDatas.get(key);
			if (activeIpData != null) {
				for (String ip : activeIpData.activeDatas.keySet()) {
					if (StringUtils.isNotBlank(ip)) {
						if (activeIpData.isIpInActive(ip)) {
							inActiveIps.add(ip);
						}
					}
				}

				if (ipInfos != null && !ipInfos.isEmpty()) {
					for (IpInfo ipInfo : ipInfos) {
						if (!activeIpData.activeDatas.containsKey(ipInfo.getIp())) {
							inActiveIps.add(ipInfo.getIp());
						}
					}
				}
			}
		}
		return inActiveIps;
	}

	public void removeActiveIp(T key, String ip) {
		if (resourceActiveIpDatas.containsKey(key)) {
			ActiveIpData activeIpData = resourceActiveIpDatas.get(key);
			if (activeIpData.activeDatas.containsKey(ip)) {
				activeIpData.activeDatas.remove(ip);
			}
		}
	}

	public List<IpInfo> getRelatedIpInfo(T key, List<IpInfo> ipInfos, Set<String> statsDataIps) {
		Set<String> inActiveIps = this.getInActiveIps(key, ipInfos);
		if (ipInfos == null || ipInfos.isEmpty()) {
			ipInfos = new ArrayList<IpInfo>();
		}
		if (statsDataIps != null && !statsDataIps.isEmpty()) {
			for (String statsDataIp : statsDataIps) {
				boolean isHasIp = false;
				for (IpInfo ipInfo : ipInfos) {
					if (statsDataIp.equals(ipInfo.getIp())) {
						isHasIp = true;
						break;
					}
				}
				if (!isHasIp) {
					ipInfos.add(new IpInfo(statsDataIp, true, true));
				}
			}
		}
		if (this.isValid()) {
			for (IpInfo ipInfo : ipInfos) {
				ipInfo.setActive(true);
			}
			if (inActiveIps != null && !inActiveIps.isEmpty()) {
				for (String inActiveIp : inActiveIps) {
					for (IpInfo ipInfo : ipInfos) {
						if (inActiveIp.equals(ipInfo.getIp())) {
							ipInfo.setActive(false);
						}
					}
				}
			}
		}

		return ipInfos;
	}

}
