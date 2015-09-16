package com.dianping.swallow.web.monitor.collector;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserProfileDto;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 下午4:02:01
 */
@Component
public class IpResourceCollector extends AbstractResourceCollector {

	private static final Logger logger = LoggerFactory.getLogger(IpResourceCollector.class);

	private static final String FACTORY_NAME = "IpResourceCollector";

	private static final String COMMA_SPLIT = ",";

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private IpResourceService ipResourceService;

	@Autowired
	private UserService baUserService;

	@Autowired
	private CmdbService cmdbService;

	private int collectorInterval = 60;

	private int delayInterval = 2;

	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@PostConstruct
	public void doScheduledTask() {
		scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					logger.info("[startTask] scheduled task running.");
					SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, IpResourceCollector.class
							.getSimpleName());
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doIpResource();
						}
					});
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, getDelayInterval(), getCollectorInterval(), TimeUnit.MINUTES);
	}

	public void doIpResource() {
		Set<String> producerIps = producerStatsDataWapper.getIps(false);
		Set<String> consuemerIps = consumerStatsDataWapper.getIps(false);
		doIpResourceTask(producerIps);
		doIpResourceTask(consuemerIps);
	}

	private void doIpResourceTask(Set<String> ips) {
		for (String ip : ips) {
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
					if (ipResource.getiPDesc() != null) {
						ipDesc.setId(ipResource.getiPDesc().getId());
					}
					ipDesc.setUpdateTime(new Date());
					addEmail(ipDesc);
					ipResource.setUpdateTime(new Date());
					ipResource.setiPDesc(ipDesc);
					ipResourceService.update(ipResource);
				}
			} else {
				List<IpResource> ipResourceInDbs = ipResourceService.findByIp(ip);
				IpResource ipResource = null;
				if (ipResourceInDbs == null || ipResourceInDbs.size() == 0) {
					ipResource = new IpResource();
					ipResource.setAlarm(false);
					ipResource.setiPDesc(new IPDesc(ip));
					ipResource.setCreateTime(new Date());
					ipResource.setUpdateTime(new Date());
					ipResource.setIp(ip);
					ipResourceService.insert(ipResource);
				}
			}
		}
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

	public int getCollectorInterval() {
		return collectorInterval;
	}

	public void setCollectorInterval(int collectorInterval) {
		this.collectorInterval = collectorInterval;
	}

	public int getDelayInterval() {
		return delayInterval;
	}

	public void setDelayInterval(int delayInterval) {
		this.delayInterval = delayInterval;
	}

}
