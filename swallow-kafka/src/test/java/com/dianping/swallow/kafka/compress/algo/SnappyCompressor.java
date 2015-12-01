package com.dianping.swallow.kafka.compress.algo;

import java.io.IOException;
import java.io.OutputStream;

import org.xerial.snappy.SnappyOutputStream;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:10:25
 */
public class SnappyCompressor extends AbstractCompressor{

	@Override
	protected OutputStream getOutputStream(OutputStream result) throws IOException {
		return new SnappyOutputStream(result);
	}

	
	public static void main(String []argc) throws IOException{
		
		SnappyCompressor compressor = new SnappyCompressor();
		compressor.compressRate();
		
	}

}
