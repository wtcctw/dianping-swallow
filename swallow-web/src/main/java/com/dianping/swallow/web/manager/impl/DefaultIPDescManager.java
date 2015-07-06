package com.dianping.swallow.web.manager.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.IPDescService;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipDescManager")
public class DefaultIPDescManager implements IPDescManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultIPDescManager.class);

	private int interval = 120;// 分钟

	private int delay = 10;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> future = null;

	@Autowired
	private IPDescService ipDescService;

	@Autowired
	private CmdbService cmdbService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@PostConstruct
	public void startTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doTask();
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, getDelay(), getInterval(), TimeUnit.HOURS);
	}

	private void doTask() {
		Set<String> ips = ipCollectorService.getIps();
		if (ips != null && ips.size() > 0) {
			@SuppressWarnings("rawtypes")
			Iterator it = ips.iterator();
			while (it.hasNext()) {
				String ip = String.valueOf(it.next());
				IPDesc ipDesc = cmdbService.getIpDesc(ip);
				if (ipDesc != null) {
					IPDesc ipDescDB = ipDescService.findByIp(ip);
					if (ipDescDB == null) {
						ipDesc.setCreateTime(new Date());
						ipDesc.setUpdateTime(new Date());
						ipDescService.insert(ipDesc);
					} else {
						ipDesc.setId(ipDescDB.getId());
						ipDesc.setUpdateTime(new Date());
						ipDescService.update(ipDesc);
					}
				}
			}
		}
	}

	public int getInterval() {
		return interval;
	}

	public int getDelay() {
		return delay;
	}
	
	@Override
	public IPDesc getIPDesc(String ip) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		IPDesc ipDesc = ipDescService.findByIp(ip);
		if (ipDesc == null) {
			ipDesc = cmdbService.getIpDesc(ip);
			ipDesc.setCreateTime(new Date());
			ipDesc.setUpdateTime(new Date());
			ipDescService.insert(ipDesc);
		}
		return ipDesc;
	}

}
