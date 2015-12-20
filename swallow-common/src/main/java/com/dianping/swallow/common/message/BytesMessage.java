package com.dianping.swallow.common.message;

import java.nio.charset.Charset;

/**
 * @author mengwenchao
 *
 * 2015年12月15日 下午4:17:41
 */
public interface BytesMessage extends Message{
	
	byte[] getBytesContent();

	void setEncoding(Charset charset);
	
}
