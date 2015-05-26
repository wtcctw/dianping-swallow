package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
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
import com.dianping.swallow.web.service.TopicService;
import com.mongodb.Mongo;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:16:09
 */
@Service("topicService")
public class TopicServiceImpl extends AbstractSwallowService implements
		TopicService {

	private static final String PRE_MSG = "msg#";
	private static final String DELIMITOR = ",";

	@Autowired
	private AdministratorDao administratorDao;

	@Autowired
	private TopicDao topicDao;

	@Autowired
	private MessageDao webSwallowMessageDao;

	@Resource(name = "administratorListService")
	private AdministratorListService administratorListService;

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
			String name, String prop, String dept) {

		return topicDao.findSpecific(start, span, name, prop, dept);
	}

	@Override
	public List<String> loadAllTopicNames(String tongXingZheng, boolean isAdmin) {
		List<String> tmpDBName = new ArrayList<String>();
		List<Mongo> allReadMongo = webSwallowMessageDao.getAllReadMongo();
		for (Mongo mc : allReadMongo) {
			tmpDBName.addAll(mc.getDatabaseNames());
		}

		List<String> dbName = new ArrayList<String>();
		for (String dbn : tmpDBName) {
			if (dbn.startsWith(PRE_MSG)) {
				String str = dbn.substring(PRE_MSG.length());
				if (!dbName.contains(str)) {
					if (isAdmin)
						dbName.add(str);
					else if (topicDao.readByName(str).getProp()
							.contains(tongXingZheng))
						dbName.add(str); // add if correlative
				}
			} else {
				// ignore
			}
		}
		return dbName;
	}

	@Override
	public void editTopic(String name, String prop, String dept, String time) {
		if (topicDao.updateTopic(name, prop, dept, time)) {
			logger.info(String.format(
					"Edit s% to [prop: s%, dept: s%, time: s%] successfully",
					name, prop, dept, time));
		} else {
			logger.info(String.format(
					"Edit s% to [prop: s%, dept: s%, time: s%] failed", name,
					prop, dept, time));
		}
	}

	@Override
	public Map<String, Object[]> getPropAndDept() {
		Map<String, Object[]> map = new HashMap<String, Object[]>();
		Set<String> proposal = new HashSet<String>();
		Set<String> department = new HashSet<String>();
		List<Topic> topics = topicDao.findAll();

		for (Topic topic : topics) {
			proposal.addAll(getPropList(topic));
			department.addAll(getDeptList(topic));
		}
		map.put("prop", proposal.toArray());
		map.put("dept", department.toArray());
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

	private Set<String> getDeptList(Topic topic) {
		Set<String> depts = new HashSet<String>();
		String dept = topic.getDept();
		if (!StringUtils.isEmpty(dept))
			depts.add(dept);
		return depts;
	}

}