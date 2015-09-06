package com.dianping.swallow.common.internal.codec;

import java.nio.charset.Charset;

/**
 * @author mengwenchao
 *
 * 2015年8月28日 下午6:49:13
 */
public interface Codec {

	static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	Object encode(Object toEncode);
	
	
	Object decode(Object toDecode);

}
