package com.dianping.swallow.common.internal.util;


import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author mengwenchao
 *
 * 2014年12月8日 下午6:55:09
 */
public class IOUtilsWrapper {
	
    protected static final String DEFAULT_ENCODING = "UTF-8";

	public static String convetStringFromRequest(InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream, DEFAULT_ENCODING);
	}

	/**
	 * @param ins
	 * @param encoding
	 * @return
	 * @throws IOException 
	 */
	public static String convetStringFromRequest(InputStream inputStream,
			String encoding) throws IOException {
		
		return IOUtils.toString(inputStream, DEFAULT_ENCODING);
	}

}
