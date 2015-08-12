package com.dianping.swallow.web.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserProfileDto;
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
public class IPDescManagerImpl implements IPDescManager {

	private static final Logger logger = LoggerFactory.getLogger(IPDescManagerImpl.class);

	private static final String COMMA_SPLIT = ",";

	private int interval = 60;// 分钟

	private int delay = 10;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> future = null;

	@Autowired
	private IPDescService ipDescService;

	@Autowired
	private CmdbService cmdbService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private UserService baUserService;

	@PostConstruct
	public void startTask() {
		setFuture(scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doTask();
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, getDelay(), getInterval(), TimeUnit.MINUTES));
	}

	private void doTask() {
		Set<String> ips = ipCollectorService.getStatisIps();
		if (ips != null && ips.size() > 0) {
			Iterator<String> iterator = ips.iterator();
			while (iterator.hasNext()) {
				String ip = iterator.next();
				IPDesc ipDesc = cmdbService.getIpDesc(ip);
				if (ipDesc != null) {
					IPDesc ipDescDB = ipDescService.findByIp(ip);
					if (ipDescDB == null) {
						ipDesc.setCreateTime(new Date());
						ipDesc.setUpdateTime(new Date());
						addEmail(ipDesc);
						ipDescService.insert(ipDesc);
					} else {
						ipDesc.setId(ipDescDB.getId());
						ipDesc.setUpdateTime(new Date());
						addEmail(ipDesc);
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
			if (ipDesc == null) {
				return null;
			}
			addEmail(ipDesc);
			ipDesc.setCreateTime(new Date());
			ipDesc.setUpdateTime(new Date());
			ipDescService.insert(ipDesc);
		}
		return ipDesc;
	}

	private void addEmail(IPDesc ipDesc) {
		try {
			if (ipDesc == null) {
				return;
			}
			String strDpMobile = ipDesc.getDpMobile();
			Set<String> dpEmails = getEmailsByStrMobile(strDpMobile);
			
			if (StringUtils.isNotBlank(ipDesc.getEmail())) {
				ipDesc.setEmail(ipDesc.getEmail() + COMMA_SPLIT + convertSetToEmail(dpEmails));
			} else {
				ipDesc.setEmail(convertSetToEmail(dpEmails));
			}

			String strOpMobile = ipDesc.getOpMobile();
			Set<String> opEmails = getEmailsByStrMobile(strOpMobile);

			if (StringUtils.isNotBlank(ipDesc.getOpEmail())) {
				ipDesc.setEmail(ipDesc.getOpEmail() + COMMA_SPLIT + convertSetToEmail(opEmails));
			} else {
				ipDesc.setEmail(convertSetToEmail(opEmails));
			}
		} catch (Exception e) {
			logger.error("[addEmail]", e);
		}
	}

	private String convertSetToEmail(Set<String> emails) {
		String strEmail = StringUtils.EMPTY;
		if (emails != null) {
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				String email = iterator.next();
				if (StringUtils.isNotBlank(email.trim())) {
					strEmail += (email.trim() + COMMA_SPLIT);
				}

			}
		}
		if (StringUtils.isNotBlank(strEmail)) {
			return strEmail.substring(0, strEmail.length() - 1);
		}
		return strEmail;
	}

	private Set<String> getEmailsByStrMobile(String strMobile) {
		String mobiles[] = null;
		if (StringUtils.isNotBlank(strMobile)) {
			mobiles = strMobile.split(COMMA_SPLIT);
		}
		Set<String> emails = null;
		if (mobiles != null && mobiles.length > 0) {
			emails = new HashSet<String>();
			for (String mobile : mobiles) {
				if (StringUtils.isNotBlank(mobile)) {
					emails.addAll(getEmailsByMobile(mobile.trim()));
				}
			}
		}
		return emails;
	}

	private Set<String> getEmailsByMobile(String mobile) {
		logger.info("[getEmailsByMobile] mobile {}", mobile);
		List<UserProfileDto> userInfos = baUserService.getEmployeeInfoByKeyword(mobile);
		Set<String> emails = new HashSet<String>();
		if (userInfos != null) {
			for (UserProfileDto userInfo : userInfos) {
				if (StringUtils.isNotBlank(userInfo.getEmail())) {
					emails.add(userInfo.getEmail().trim());
				}
			}
		}
		logger.info("[getEmailsByMobile] emails {}", emails);
		return emails;
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
