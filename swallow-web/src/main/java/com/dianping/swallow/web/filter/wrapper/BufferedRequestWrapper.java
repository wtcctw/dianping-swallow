package com.dianping.swallow.web.filter.wrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author mingdongli
 *
 *         2015年9月25日上午10:36:46
 */
public class BufferedRequestWrapper extends HttpServletRequestWrapper {

	ByteArrayInputStream bais;

	ByteArrayOutputStream baos;

	BufferedServletInputStream bsis;

	byte[] buffer;

	public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
		super(req);
		InputStream is = req.getInputStream();
		baos = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int letti;
		while ((letti = is.read(buf)) > 0) {
			baos.write(buf, 0, letti);
		}
		buffer = baos.toByteArray();
	}

	public ServletInputStream getInputStream() {
		try {
			bais = new ByteArrayInputStream(buffer);
			bsis = new BufferedServletInputStream(bais);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return bsis;
	}

	public byte[] getBuffer() {
		return buffer;
	}

}