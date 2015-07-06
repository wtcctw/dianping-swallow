package com.dianping.swallow.web.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.IPCollectorService;

@Service("ipCollectorService")
public class IPCollectorServiceImpl implements IPCollectorService {
	
	private Set<String> ips = new HashSet<String>();

	@Override
	public boolean addIp(String ip) {
		return ips.add(ip);
	}

}
