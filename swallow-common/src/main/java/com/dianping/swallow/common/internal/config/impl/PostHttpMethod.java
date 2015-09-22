package com.dianping.swallow.common.internal.config.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
 *         2015年9月22日下午6:22:43
 */
public class PostHttpMethod implements HttpMethod {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public <T extends LionRet> T setValue(String urlAddress, Class<T> clazz) {

		if (logger.isDebugEnabled()) {
			logger.debug("[executePost]" + urlAddress);
		}
		if (logger.isInfoEnabled()) {
			logger.info("[executePost]" + urlAddress);
		}

		String[] urlParam = urlAddress.split("\\?");
		if (urlParam.length != 2) {
			throw new IllegalArgumentException("illegal urlAddress :" + urlAddress);
		}
		URL url;
		StringBuffer result = new StringBuffer();
		HttpURLConnection connection = null;
		try {
			url = new URL(urlParam[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(3000);

			OutputStream os = connection.getOutputStream();
			os.write(urlParam[1].getBytes("UTF-8"));
			os.flush();
			os.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			logger.error("[executePost]" + urlAddress, e);
			throw new IllegalStateException("io exception", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return JsonBinder.getNonEmptyBinder().fromJson(result.toString(), clazz);

	}

}
