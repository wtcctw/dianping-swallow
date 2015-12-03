package com.dianping.swallow.kafka.compress.algo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:10:25
 */
public class GzipCompressor extends AbstractCompressor{

	@Override
	protected OutputStream getOutputStream(OutputStream result) throws IOException {
		
		GZIPOutputStream gous = new GZIPOutputStream(result);
		return gous;
	}

	@Override
	public String desc() {
		return "gzip";
	}

	@Override
	protected InputStream getInputStream(byte[] input) throws IOException {

		return new GZIPInputStream(new ByteArrayInputStream(input));
	}
	
}
