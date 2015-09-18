package com.dianping.swallow.web.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserProfileDto;
import com.dianping.swallow.web.manager.IPResourceManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.IpResourceService;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipDescManager")
public class IPResourceManagerImpl implements IPResourceManager {

	private static final Logger logger = LoggerFactory.getLogger(IPResourceManagerImpl.class);

	private static final String COMMA_SPLIT = ",";

	@Autowired
	private IpResourceService ipResourceService;

	@Autowired
	private CmdbService cmdbService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private UserService baUserService;

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
			ipResource.setAlarm(true);
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

			String strOtherDpEmail = convertSetToEmail(dpEmails);
			if (StringUtils.isNotBlank(strOtherDpEmail)) {
				if (StringUtils.isNotBlank(ipDesc.getEmail())) {
					ipDesc.setEmail(ipDesc.getEmail() + COMMA_SPLIT + strOtherDpEmail);
				} else {
					ipDesc.setEmail(strOtherDpEmail);
				}
			}

			String strOpMobile = ipDesc.getOpMobile();
			Set<String> opEmails = getEmailsByStrMobile(strOpMobile);
			String strOtherOpEmail = convertSetToEmail(opEmails);
			if (StringUtils.isNotBlank(strOtherOpEmail)) {
				if (StringUtils.isNotBlank(ipDesc.getOpEmail())) {
					ipDesc.setOpEmail(ipDesc.getOpEmail() + COMMA_SPLIT + strOtherOpEmail);
				} else {
					ipDesc.setOpEmail(strOtherOpEmail);
				}
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

}
