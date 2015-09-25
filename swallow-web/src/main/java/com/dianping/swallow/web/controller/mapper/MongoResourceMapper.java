package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.MongoResourceDto;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;


/**
 * @author mingdongli
 *
 * 2015年9月18日上午9:24:23
 */
public class MongoResourceMapper {
	
	private static final String DELIMITOR = ",";

	public static MongoResource toMongoResource(MongoResourceDto dto) {

		MongoResource mongoResource = new MongoResource();

		mongoResource.setId(dto.getId());
		mongoResource.setIp(dto.getIp());
		mongoResource.setCatalog(dto.getCatalog());
		mongoResource.setDisk(dto.getDisk());
		mongoResource.setLoad(dto.getLoad());
		mongoResource.setQps(dto.getQps());
		mongoResource.setMongoType(MongoType.valueOf(dto.getMongoType()));
		
		List<String> topicList = new ArrayList<String>();
		String topics = dto.getTopics();
		
		if (StringUtils.isNotBlank(topics)) {
			String[] topicLists = topics.split(DELIMITOR);
			for (String wl : topicLists) {
				if (!topicList.contains(wl)) {
					topicList.add(wl);
				}
			}
		}

		return mongoResource;
	}

	public static MongoResourceDto toMongoResourceDto(MongoResource mongoResource) {

		MongoResourceDto dto = new MongoResourceDto();

		dto.setId(mongoResource.getId());
		dto.setIp(mongoResource.getIp());
		dto.setCatalog(mongoResource.getCatalog());
		dto.setDisk(mongoResource.getDisk());
		dto.setLoad(mongoResource.getLoad());
		dto.setQps(mongoResource.getQps());
		dto.setMongoType(mongoResource.getMongoType().name());
		
		List<String> list = mongoResource.getTopics();
		dto.setTopics(StringUtils.join(list, DELIMITOR));

		return dto;
	}
}
