package com.dianping.swallow.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.dao.WebSwallowMessageDao;
import com.dianping.swallow.web.model.Topic;
import com.mongodb.MongoClient;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:16:09
 */
@Service("topicService")
public class TopicServiceImpl extends AbstractSwallowService implements TopicService {

	private static final String PRE_MSG = "msg#";
	private static final String DELIMITOR = ",";

	@Autowired
	private TopicDao tdi;
	@Autowired
	private WebSwallowMessageDao smdi;

	// read records from writeMongoOps dut to it alread exists
	@Override
	public Map<String, Object> getAllTopicFromExisting(int start, int span) {
		return tdi.findFixedTopic(start, span);
	}

	// just read, so use writeMongoOps
	@Override
	public Map<String, Object> getSpecificTopic(int start, int span,
			String name, String prop, String dept) {

		return tdi.findSpecific(start, span, name, prop, dept);
	}

	@Override
	public List<String> getTopicNames() {
		List<String> tmpDBName = new ArrayList<String>();
		List<MongoClient> allReadMongo = smdi.getAllReadMongo();
		for (MongoClient mc : allReadMongo) {
			tmpDBName.addAll(mc.getDatabaseNames());
		}

		List<String> dbName = new ArrayList<String>();
		for (String dbn : tmpDBName) {
			if (dbn.startsWith(PRE_MSG)) {
				String str = dbn.substring(PRE_MSG.length());
				if (!dbName.contains(str))
					dbName.add(str);
			}
		}
		return dbName;
	}

	@Override
	public void editTopic(String name, String prop, String dept, String time) {

			tdi.updateTopic(name, prop, dept, time);
			if (logger.isInfoEnabled()) {
				logger.info("Update prop to " + splitProps(prop) + " of "
						+ name);
			}

		return;
	}

	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

	// read from writeMongoOps, everytime read the the database to get the
	// latest info
	@Override
	public Set<String> getPropAndDept() {
		Set<String> propdept = new HashSet<String>();
		List<Topic> topics = tdi.findAll();

		for (Topic topic : topics) {
			propdept.addAll(getPropList(topic));
			propdept.addAll(getDeptList(topic));
		}
		return propdept;
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
