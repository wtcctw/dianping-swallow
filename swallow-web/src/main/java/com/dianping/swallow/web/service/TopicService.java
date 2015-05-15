package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*-
 * @author mingdongli
 *
 * 2015年5月14日下午6:05:45
 */
public interface TopicService extends SwallowService {

	Map<String, Object> getAllTopicFromExisting(int start, int span);

	Map<String, Object> getSpecificTopic(int start, int span, String name,
			String prop, String dept);

	List<String> getTopicNames();

	void editTopic(String name, String prop, String dept, String time);

	Set<String> getPropAndDept();
}
