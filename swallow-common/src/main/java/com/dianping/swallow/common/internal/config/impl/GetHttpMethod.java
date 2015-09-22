package com.dianping.swallow.common.internal.config.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.HttpMethod;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl.LionRet;

/**
 * @author mingdongli
 *
 *         2015年9月22日下午6:16:48
 */
public class GetHttpMethod implements HttpMethod {
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	@Override
	public <T extends LionRet> T setValue(String urlAddress, Class<T> clazz) {

		if (logger.isDebugEnabled()) {
			logger.debug("[executeGet]" + urlAddress);
		}
		if (logger.isInfoEnabled()) {
			logger.info("[executeGet]" + urlAddress);
		}

		URL url;
		StringBuffer result = new StringBuffer();
		HttpURLConnection connection = null;
		try {
			url = new URL(urlAddress);
			connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			logger.error("[executeGet]" + urlAddress, e);
			throw new IllegalStateException("io exception", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return JsonBinder.getNonEmptyBinder().fromJson(result.toString(), clazz);

	}

}
