package com.dianping.swallow.web.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;

/**
 * 
 * @author qiyin
 *
 *         2015年9月24日 下午1:18:11
 */
public class GsonUtil {

	public static final Gson gson = new Gson();

	public static String toJson(Object src) {
		return gson.toJson(src);
	}

	public static <T> T fromJson(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}

	public static <T> T fromJson(String json, Type type) {
		return gson.fromJson(json, type);
	}

}
