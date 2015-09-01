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
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.manager.IPResourceManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipDescManager")
public class IPResourceManagerImpl implements IPResourceManager {

	private static final Logger logger = LoggerFactory.getLogger(IPResourceManagerImpl.class);

	private static final String COMMA_SPLIT = ",";

	private static final String FACTORY_NAME = "IPDescManager";

	private int interval = 120;// 分钟

	private int delay = 10;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	private ScheduledFuture<?> future = null;

	// @Autowired
	// /private IPDescService ipDescService;

	@Autowired
	private IpResourceService ipResourceService;

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
					logger.info("[startTask] scheduled task running.");
					SwallowActionWrapper catWrapper = new CatActionWrapper("IPDescManagerImpl", "doIpDescTask");
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doIpDescTask();
						}
					});
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, getDelay(), getInterval(), TimeUnit.MINUTES));
	}

	private void doIpDescTask() {
		Set<String> ips = ipCollectorService.getStatisIps();
		if (ips != null && ips.size() > 0) {
			Iterator<String> iterator = ips.iterator();
			while (iterator.hasNext()) {
				String ip = iterator.next();
				IPDesc ipDesc = cmdbService.getIpDesc(ip);
				if (ipDesc != null) {
					List<IpResource> ipResourceInDbs = ipResourceService.findByIp(ip);
					if (ipResourceInDbs == null || ipResourceInDbs.size() == 0) {
						ipDesc.setCreateTime(new Date());
						ipDesc.setUpdateTime(new Date());
						addEmail(ipDesc);
						IpResource ipResource = new IpResource();
						ipResource.setIp(ipDesc.getIp());
						ipResource.setCreateTime(new Date());
						ipResource.setUpdateTime(new Date());
						ipResource.setiPDesc(ipDesc);
						ipResourceService.insert(ipResource);
					} else {
						IpResource ipResource = ipResourceInDbs.get(0);
						ipResource.setId(ipResource.getId());
						if (ipResource.getiPDesc() != null) {
							ipDesc.setId(ipResource.getiPDesc().getId());
						}
						ipDesc.setUpdateTime(new Date());
						addEmail(ipDesc);
						ipResource.setUpdateTime(new Date());
						ipResource.setiPDesc(ipDesc);
						ipResourceService.update(ipResource);
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
		List<IpResource> ipResources = ipResourceService.findByIp(ip);
		if (ipResources == null || ipResources.size() == 0) {
			IPDesc ipDesc = cmdbService.getIpDesc(ip);
			if (ipDesc == null) {
				return null;
			}
			addEmail(ipDesc);
			ipDesc.setCreateTime(new Date());
			ipDesc.setUpdateTime(new Date());
			IpResource ipResource = new IpResource();
			ipResource.setIp(ip);
			ipResource.setiPDesc(ipDesc);
			ipResource.setCreateTime(new Date());
			ipResource.setUpdateTime(new Date());
			ipResourceService.insert(ipResource);
		}
		return ipResources.get(0).getiPDesc();
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
				ipDesc.setOpEmail(ipDesc.getOpEmail() + COMMA_SPLIT + convertSetToEmail(opEmails));
			} else {
				ipDesc.setOpEmail(convertSetToEmail(opEmails));
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
