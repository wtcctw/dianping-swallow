package com.dianping.swallow.web.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.message.JsonSerializedException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author qiyin
 *
 *         2015年9月21日 下午3:42:25
 */
public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper om = new ObjectMapper();

	public static <T> T fromJson(String content, Class<T> valueType) {
		T result = null;
		try {
			result = om.readValue(content, valueType);
		} catch (JsonParseException e) {
			logger.error("[getObject] JsonParseException.", e);
		} catch (JsonMappingException e) {
			logger.error("[getObject] JsonMappingException.", e);
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
}
