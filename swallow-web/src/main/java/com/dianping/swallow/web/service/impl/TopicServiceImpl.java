package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;
import org.apache.commons.lang.StringUtils;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:16:09
 */
@Service("topicService")
public class TopicServiceImpl extends AbstractSwallowService implements TopicService {

	private static final String DELIMITOR = ",";

	@Autowired
	private TopicDao topicDao;

	@Resource(name = "userService")
	private UserService userService;

	private Map<String, Set<String>> topicToWhiteList = new ConcurrentHashMap<String, Set<String>>();

	@Override
	public Pair<Long, List<Topic>> loadTopicPage(int start, int span) {
		
		return topicDao.loadTopicPage(start, span);
	}

	@Override
	public Pair<Long, List<Topic>> loadSpecificTopicPage(int start, int span, String name, String prop) {

		return topicDao.loadSpecificTopicPage(start, span, name, prop);
	}

	@Override
	public List<String> loadTopicNames(String proposal, boolean isAdmin) {

		Map<String, Set<String>> topicToWhiteList = this.loadTopicToWhiteList();
		if (isAdmin) {
			return new ArrayList<String>(topicToWhiteList.keySet());
		} else {
			List<String> topics = new ArrayList<String>();
			for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
				if (entry.getValue().contains(proposal)) {
					String topic = entry.getKey();
					if (!topics.contains(topic)) {
						topics.add(topic);
					}
				}
			}
			return topics;
		}
	}

	@Override
	public int editTopic(String name, String prop, String time) throws MongoSocketException, MongoException{

		Set<String> proposal = splitProps(prop);
		this.loadTopicToWhiteList().put(name, proposal);
		StringBuffer sb = new StringBuffer();
		boolean first = false;
		for(String p : proposal){
			if(!first){
				sb.append(p);
				first = true;
			}else{
				sb.append(",").append(p);
			}
		}
		return topicDao.updateTopic(name, sb.toString(), time);
	}

	@Override
	public String[] loadTopicProposal(String username, boolean isAdmin) {

		Set<String> proposal = new HashSet<String>();
		List<Topic> topics = topicDao.LoadTopics();

		if (isAdmin) {
			for (Topic topic : topics) {
				proposal.addAll(getPropList(topic));
			}
		} else {
			for (Topic topic : topics) {
				Set<String> tmpprop = getPropList(topic);
				if (tmpprop.contains(username)) {
					proposal.addAll(tmpprop);
				}
			}

		}

		List<Administrator> adminList = userService.loadUsers();
		for(Administrator admin : adminList){
			proposal.add(admin.getName());
		}
		
		return proposal.toArray(new String[proposal.size()]);
	}

	private Set<String> getPropList(Topic topic) {
		Set<String> props = new HashSet<String>();
		String[] tmpprops = topic.getProp().split(DELIMITOR);

		for (String tmpProp : tmpprops) {
			if (!StringUtils.isEmpty(tmpProp)) {
				props.add(tmpProp);
			}
		}
		return props;
	}

	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

	@Override
	public Map<String, Set<String>> loadTopicToWhiteList() {
		return topicToWhiteList;
	}

	@Override
	public int saveTopic(Topic topic) {
		
		if(StringUtils.isNotBlank(topic.getName())){
			return topicDao.saveTopic(topic);
		}
		return ResponseStatus.TOPICBLANK.getStatus();
	}

	@Override
	public Topic loadTopicByName(String name) {
		return topicDao.readByName(name);
	}

	@Override
	public List<String> loadTopicNames(String username) {

		List<String> topics = new ArrayList<String>();
		for(Map.Entry<String, Set<String>> entry:topicToWhiteList.entrySet()){
			if(entry.getValue().contains(username)){
				topics.add(entry.getKey());
			}
		}
		return topics;
	}

}
