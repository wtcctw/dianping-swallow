package com.dianping.swallow.web.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoStatus;
import com.dianping.swallow.common.message.JsonDeserializedException;
import com.dianping.swallow.common.message.JsonSerializedException;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * 
 * @author qiyin
 *
 *         2015年9月21日 下午3:42:25
 */
public class JsonUtil {

	private static final Logger logger = LogManager.getLogger(JsonUtil.class);

	private static ObjectMapper om = new ObjectMapper();

	static {
		om = new ObjectMapper();
		// 序列化时，忽略空的bean(即沒有任何Field)
		om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 序列化时，忽略在JSON字符串中存在但Java对象实际没有的属性
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		om.setFilters(new SimpleFilterProvider().addFilter("mongoStatusFilter",
				SimpleBeanPropertyFilter.serializeAllExcept("mongoClientOptions")));
		om.addMixInAnnotations(MongoStatus.class, MongoStatusFilter.class);
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	public static <T> T fromJson(String content, TypeReference<T> typeReference) {

		try {
			return om.readValue(content, typeReference);
		} catch (IOException e) {
			throw new JsonDeserializedException("Deserialized json string error : " + content, e);
		}
	}

	public static <T> T fromJson(String content, Class<T> clazz) {
		T result = null;
		try {
			result = om.readValue(content, clazz);
		} catch (IOException e) {
			logger.error("[getObject] IOException.", e);
		}
		return result;
	}

	public static String toJson(Object object) {
		try {
			return om.writeValueAsString(object);
		} catch (IOException e) {
			throw new JsonSerializedException("Serialized Object to json string error : " + object, e);
		}
	}

	@JsonFilter("mongoStatusFilter")
	private static interface MongoStatusFilter {
		
	}
}
