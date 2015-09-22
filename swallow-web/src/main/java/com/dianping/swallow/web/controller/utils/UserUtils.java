package com.dianping.swallow.web.controller.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.TopicController;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;

/**
 * @author mingdongli
 *
 *         2015年9月1日下午7:04:53
 */
@Component
public class UserUtils {

	private static final String LOGINDELIMITOR = "\\|";

	private static final String ALL = "all";

	private static final String CONSUMERID = "consumerId";

	private static final String CONSUMERIP = "consumerIps";

	private static final String IP = "ip";

	private static final String APPLICATION = "iPDesc.name";

	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;

	public String getUsername(HttpServletRequest request) {
		String tmpusername = request.getRemoteUser();

		if (tmpusername == null) {
			return "";
		} else {
			String[] userinfo = tmpusername.split(LOGINDELIMITOR);
			return userinfo[0];
		}
	}

	public boolean isAdministrator(String username) {

		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username) || adminSet.contains(ALL);
		return isAdmin;
	}

	public boolean isTrueAdministrator(String username) {

		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username);
		return isAdmin;
	}

	public List<String> topicNames(String username) {

		List<String> topics = null;
		Map<String, Set<String>> topicToWhiteList = topicResourceService.loadCachedTopicToAdministrator();
		boolean findAll = isAdministrator(username);

		if (findAll) {
			topics = new ArrayList<String>(topicToWhiteList.keySet());
			if (isTrueAdministrator(username)) {
				topics.add(TopicController.DEFAULT);
			}
		} else {
			topics = new ArrayList<String>();
			for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
				if (entry.getValue().contains(username)) {
					String topic = entry.getKey();
					if (!topics.contains(topic)) {
						topics.add(topic);
					}
				}
			}
		}

		return topics;
	}

	public List<String> consumerIds(String username) {

		List<String> consumerIds = new ArrayList<String>();

		if (!isAdministrator(username)) {
			List<String> topics = topicNames(username);
			String topicString = StringUtils.join(topics, ",");

			ConsumerIdParam consumerIdParam = new ConsumerIdParam();
			consumerIdParam.setTopic(topicString);
			consumerIdParam.setConsumerId("");
			consumerIdParam.setConsumerIp("");
			consumerIdParam.setLimit(Integer.MAX_VALUE);
			consumerIdParam.setOffset(0);

			Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
			if (pair.getFirst() > 0) {
				for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
					String id = consumerIdResource.getConsumerId();
					if (StringUtils.isNotBlank(id) && !consumerIds.contains(id)) {
						consumerIds.add(id);
					}
				}
			}
		} else {
			List<ConsumerIdResource> consumerIdResources = consumerIdResourceService.findAll(CONSUMERID);

			for (ConsumerIdResource consumerIdResource : consumerIdResources) {
				String cid = consumerIdResource.getConsumerId();
				if (!consumerIds.contains(cid)) {
					consumerIds.add(cid);
				}
			}
		}

		if (isTrueAdministrator(username)) {
			consumerIds.remove(TopicController.DEFAULT);
		}

		return consumerIds;

	}

	public List<String> consumerIps(String username) {

		Set<String> consumerIps = new HashSet<String>();

		if (!isAdministrator(username)) {
			List<String> topics = topicNames(username);
			String topicString = StringUtils.join(topics, ",");

			ConsumerIdParam consumerIdParam = new ConsumerIdParam();
			consumerIdParam.setTopic(topicString);
			consumerIdParam.setConsumerId("");
			consumerIdParam.setConsumerIp("");
			consumerIdParam.setLimit(Integer.MAX_VALUE);
			consumerIdParam.setOffset(0);

			Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
			if (pair.getFirst() > 0) {
				for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
					List<String> ips = consumerIdResource.getConsumerIps();
					if (!ips.isEmpty()) {
						consumerIps.addAll(ips);
					}
				}
			}
		} else {
			List<ConsumerIdResource> consumerIdResources = consumerIdResourceService.findAll(CONSUMERIP);

			for (ConsumerIdResource consumerIdResource : consumerIdResources) {
				List<String> cips = consumerIdResource.getConsumerIps();
				if (!cips.isEmpty()) {
					consumerIps.addAll(cips);
				}
			}
		}

		return new ArrayList<String>(consumerIps);

	}

	public Pair<List<String>, Set<String>> consumerIdAndIps(String username) {

		Set<String> consumerIps = new HashSet<String>();
		List<String> consumerIds = new ArrayList<String>();

		if (!isAdministrator(username)) {
			List<String> topics = topicNames(username);
			String topicString = StringUtils.join(topics, ",");
			ConsumerIdParam consumerIdParam = new ConsumerIdParam();
			consumerIdParam.setTopic(topicString);
			consumerIdParam.setConsumerId("");
			consumerIdParam.setConsumerIp("");
			consumerIdParam.setLimit(Integer.MAX_VALUE);
			consumerIdParam.setOffset(0);

			Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
			if (pair.getFirst() > 0) {
				for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
					List<String> list = consumerIdResource.getConsumerIps();
					if (!list.isEmpty()) {
						consumerIps.addAll(list);
					}

					String id = consumerIdResource.getConsumerId();
					if (StringUtils.isNotBlank(id) && !consumerIds.contains(id)) {
						consumerIds.add(id);
					}
				}
			}
		} else {
			List<ConsumerIdResource> consumerIdResources = consumerIdResourceService.findAll(CONSUMERID, CONSUMERIP);

			for (ConsumerIdResource consumerIdResource : consumerIdResources) {
				String cid = consumerIdResource.getConsumerId();
				if (!consumerIds.contains(cid)) {
					consumerIds.add(cid);
				}
				List<String> cips = consumerIdResource.getConsumerIps();
				if (!cips.isEmpty()) {
					consumerIps.addAll(cips);
				}
			}
		}

		return new Pair<List<String>, Set<String>>(consumerIds, consumerIps);

	}

	public List<String> producerIps(String username) {

		Set<String> producerIp = new HashSet<String>();

		List<TopicResource> topicResources = topicResourceService.findAll();
		List<String> tmpips;
		if (!isAdministrator(username)) {
			for (TopicResource topicResource : topicResources) {
				String admin = topicResource.getAdministrator();
				if(StringUtils.isNotBlank(admin)){
					String[] adminArray = admin.split(",");
					if(Arrays.asList(adminArray).contains(username)){
						tmpips = topicResource.getProducerIps();
						if(tmpips != null){
							producerIp.addAll(tmpips);
						}
					}
				}
			}
		}else{
			for (TopicResource topicResource : topicResources) {
				tmpips = topicResource.getProducerIps();
				if (tmpips != null) {
					producerIp.addAll(tmpips);
				}
			}
		}
		
		return new ArrayList<String>(producerIp);
	}

	public List<String> ips(String username) {

		Set<String> ips = new HashSet<String>();

		if (isAdministrator(username)) {
			List<IpResource> ipResources = ipResourceService.findAll(IP);

			for (IpResource ipResource : ipResources) {
				String ip = ipResource.getIp();
				if (!ips.contains(ip)) {
					ips.add(ip);
				}
			}
		}else{
			List<String> producerIps = producerIps(username);
			List<String> consumerIps = consumerIps(username);
			ips.addAll(consumerIps);
			ips.addAll(producerIps);
		}

		return new ArrayList<String>(ips);
	}

	public List<String> applications(String username) {
		Set<String> apps = new HashSet<String>();

		if (!isAdministrator(username)) {
			List<String> ipList = ips(username);
			Pair<Long, List<IpResource>> pair = ipResourceService.findByIp(0, Integer.MAX_VALUE, false,
					ipList.toArray(new String[ipList.size()]));
			if (pair.getFirst() > 0) {
				for (IpResource ipResource : pair.getSecond()) {
					IPDesc iPDesc = ipResource.getiPDesc();
					if (iPDesc != null) {
						String app = iPDesc.getName();
						if (StringUtils.isNotBlank(app) && !apps.contains(app)) {
							apps.add(app);
						}
					}
				}
			}
		} else {
			List<IpResource> ipResources = ipResourceService.findAll(APPLICATION);

			for (IpResource ipResource : ipResources) {
				IPDesc iPDesc = ipResource.getiPDesc();
				if (iPDesc != null) {
					String app = iPDesc.getName();
					if (StringUtils.isNotBlank(app) && !apps.contains(app)) {
						apps.add(app);
					}
				}
			}
		}

		return new ArrayList<String>(apps);
	}
}
