package com.dianping.swallow.web.filter.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;


/**
 * @author mingdongli
 *
 * 2015年9月25日上午10:34:23
 */
public class ByteArrayServletStream extends ServletOutputStream {

	ByteArrayOutputStream baos;

	ByteArrayServletStream(ByteArrayOutputStream baos) {
		this.baos = baos;
	}

	public void write(int param) throws IOException {
		baos.write(param);
	}
}
