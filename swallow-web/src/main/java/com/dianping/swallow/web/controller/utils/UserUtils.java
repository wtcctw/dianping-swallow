package com.dianping.swallow.web.controller.utils;

import java.util.ArrayList;
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
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;


/**
 * @author mingdongli
 *
 * 2015年9月1日下午7:04:53
 */
@Component
public class UserUtils {

	private static final String LOGINDELIMITOR = "\\|";
	
	private static final String ALL = "all";
	
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
	
	public boolean isAdministrator(String username){
		
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username) || adminSet.contains(ALL);
		return isAdmin;
	}

	public boolean isTrueAdministrator(String username){
		
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username);
		return isAdmin;
	}

	public List<String> topicNames(String username){
		
		List<String> topics = null;
		Map<String, Set<String>> topicToWhiteList = topicResourceService.loadCachedTopicToWhiteList();
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
	
	public List<String> consumerIds(String username){
		
		List<String> consumerIds = new ArrayList<String>();
		
		List<String> topics = topicNames(username);
		String topicString = StringUtils.join(topics, ",");
		
		ConsumerIdParam consumerIdParam = new ConsumerIdParam();
		consumerIdParam.setTopic(topicString);
		consumerIdParam.setConsumerId("");
		consumerIdParam.setConsumerIp("");
		consumerIdParam.setLimit(Integer.MAX_VALUE);
		consumerIdParam.setOffset(0);
		
		Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
		if(pair.getFirst() > 0){
			for(ConsumerIdResource consumerIdResource : pair.getSecond()){
				String id = consumerIdResource.getConsumerId();
				if(StringUtils.isNotBlank(id) && !consumerIds.contains(id)){
					consumerIds.add(id);
				}
			}
		}
		
		return consumerIds;
		
	}
	
	public List<String> consumerIps(String username){
		
		Set<String> consumerIps = new HashSet<String>();
		
		List<String> topics = topicNames(username);
		String topicString = StringUtils.join(topics, ",");
		
		ConsumerIdParam consumerIdParam = new ConsumerIdParam();
		consumerIdParam.setTopic(topicString);
		consumerIdParam.setConsumerId("");
		consumerIdParam.setConsumerIp("");
		consumerIdParam.setLimit(Integer.MAX_VALUE);
		consumerIdParam.setOffset(0);
		
		Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
		if(pair.getFirst() > 0){
			for(ConsumerIdResource consumerIdResource : pair.getSecond()){
				List<String> ips = consumerIdResource.getConsumerIps();
				if(!ips.isEmpty()){
					consumerIps.addAll(ips);
				}
			}
		}
		
		return new ArrayList<String>(consumerIps);
		
	}
	
	public Pair<List<String>, Set<String>> consumerIdAndIps(String username){
		
		Set<String> consumerIps = new HashSet<String>();
		List<String> consumerIds = new ArrayList<String>();
		
		List<String> topics = topicNames(username);
		String topicString = StringUtils.join(topics, ",");
		ConsumerIdParam consumerIdParam = new ConsumerIdParam();
		consumerIdParam.setTopic(topicString);
		consumerIdParam.setConsumerId("");
		consumerIdParam.setConsumerIp("");
		consumerIdParam.setLimit(Integer.MAX_VALUE);
		consumerIdParam.setOffset(0);
		
		Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
		if(pair.getFirst() > 0){
			for(ConsumerIdResource consumerIdResource : pair.getSecond()){
				List<String> list = consumerIdResource.getConsumerIps();
				if(!list.isEmpty()){
					consumerIps.addAll(list);
				}
				
				String id = consumerIdResource.getConsumerId();
				if(StringUtils.isNotBlank(id) && !consumerIds.contains(id)){
					consumerIds.add(id);
				}
			}
		}
		
		return new Pair<List<String>, Set<String> >(consumerIds, consumerIps);
		
	}
}
