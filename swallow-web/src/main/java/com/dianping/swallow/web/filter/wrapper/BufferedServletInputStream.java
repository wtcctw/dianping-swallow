package com.dianping.swallow.web.filter.wrapper;

import java.io.ByteArrayInputStream;

import javax.servlet.ServletInputStream;

/**
 * @author mingdongli
 *
 *         2015年9月25日上午10:36:01
 */
public class BufferedServletInputStream extends ServletInputStream {

	ByteArrayInputStream bais;

	public BufferedServletInputStream(ByteArrayInputStream bais) {
		this.bais = bais;
	}

	public int available() {
		return bais.available();
	}

	public int read() {
		return bais.read();
	}

	public int read(byte[] buf, int off, int len) {
		return bais.read(buf, off, len);
	}

}
