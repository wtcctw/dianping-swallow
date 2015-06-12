package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AdministratorListService;
import com.dianping.swallow.web.service.FilterMetaDataService;
import com.dianping.swallow.web.service.TopicService;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:16:09
 */
@Service("topicService")
public class TopicServiceImpl extends AbstractSwallowService implements
		TopicService {

	private static final String DELIMITOR = ",";

	@Autowired
	private AdministratorDao administratorDao;

	@Autowired
	private TopicDao topicDao;

	@Autowired
	private MessageDao webSwallowMessageDao;

	@Resource(name = "administratorListService")
	private AdministratorListService administratorListService;
	
	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;

	/*
	 * read records from writeMongo due to it already exists
	 */
	@Override
	public Map<String, Object> loadAllTopic(int start, int span) {
		return topicDao.findFixedTopic(start, span);
	}

	/*
	 * just read, so use writeMongo
	 */
	@Override
	public Map<String, Object> loadSpecificTopic(int start, int span,
			String name, String prop) {

		return topicDao.findSpecific(start, span, name, prop);
	}

	@Override
	public List<String> loadAllTopicNames(String tongXingZheng, boolean isAdmin) {
		
		Map<String, Set<String>> topicToWhiteList = filterMetaDataService.loadTopicToWhiteList();
		if(isAdmin){
			return new ArrayList<String>(topicToWhiteList.keySet());
		}
		else{
			List<String> topics = new ArrayList<String>();
			for(Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()){
				if(entry.getValue().contains(tongXingZheng)){
					String topic = entry.getKey();
					if(!topics.contains(topic)){
						topics.add(topic);
					}
				}
			} 
			return topics;
		}
	}

	@Override
	public boolean editTopic(String name, String prop, String time) {
		
		filterMetaDataService.loadTopicToWhiteList().put(name, splitProps(prop));
		if (topicDao.updateTopic(name, prop, time)) {
			logger.info(String.format(
					"Edit %s to [prop: %s, time: %s] successfully",
					name, prop, time));
			return true;
		} else {
			logger.info(String.format(
					"Edit %s to [prop: %s, time: %s] failed", name,
					prop, time));
			return false;
		}
	}

	@Override
	public Map<String, Object[]> getPropAndDept(String username) {
		Map<String, Object[]> map = new HashMap<String, Object[]>();
		Set<String> proposal = new HashSet<String>();
		List<Topic> topics = topicDao.findAll();

		boolean isAdmin = filterMetaDataService.loadAdminSet().contains(username);
		boolean switchenv = filterMetaDataService.isShowContentToAll();
		if(isAdmin || switchenv){
			for (Topic topic : topics) {
				proposal.addAll(getPropList(topic));
			}
		}
		else{
			for (Topic topic : topics) {
				Set<String> tmpprop = getPropList(topic);
				if(tmpprop.contains(username)){
					proposal.addAll(tmpprop);
				}
			}

		}
		
		map.put("prop", proposal.toArray());
		map.put("edit", filterMetaDataService.loadAllUsers().toArray());
		
		return map;
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

}
