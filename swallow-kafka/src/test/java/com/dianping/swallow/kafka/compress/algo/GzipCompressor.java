package com.dianping.swallow.kafka.compress.algo;

import java.io.IOException;
import java.io.OutputStream;
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
	
	public static void main(String []argc) throws IOException{
		
		GzipCompressor gzip = new GzipCompressor();
		gzip.compressRate();
		
	}
}
