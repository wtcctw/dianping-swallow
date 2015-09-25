package com.dianping.swallow.web.filter.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;


/**
 * @author mingdongli
 *
 * 2015年9月25日上午10:35:08
 */
public class ByteArrayPrintWriter {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	private PrintWriter pw = new PrintWriter(baos);

	private ServletOutputStream sos = new ByteArrayServletStream(baos);

	public PrintWriter getWriter() {
		return pw;
	}

	public ServletOutputStream getStream() {
		return sos;
	}

	public byte[] toByteArray() {
		return baos.toByteArray();
	}
}
