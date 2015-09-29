package com.dianping.swallow.web.controller.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.resource.IpInfo;


/**
 * @author mingdongli
 *
 * 2015年9月28日下午6:25:41
 */
public class IpInfoUtils {

	public static Set<String> extractIps(Collection<IpInfo> ipInfo){
		
		Set<String> ips = new HashSet<String>();
		if(ipInfo == null){
			return ips;
		}
		for(IpInfo info : ipInfo){
			ips.add(info.getIp());
		}
		
		return ips;
	}
	
	public static List<IpInfo> buildIpInfo(Collection<String> ips){
		List<IpInfo> ipInfo = new ArrayList<IpInfo>();
		for(String ip : ips){
			ipInfo.add(new IpInfo(ip, Boolean.TRUE, Boolean.TRUE));
		}
		
		return ipInfo;
	}
}
