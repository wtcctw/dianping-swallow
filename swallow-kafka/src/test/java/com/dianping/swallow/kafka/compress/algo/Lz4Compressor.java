package com.dianping.swallow.kafka.compress.algo;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.kafka.common.message.KafkaLZ4BlockOutputStream;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:10:25
 */
public class Lz4Compressor extends AbstractCompressor{

	@Override
	protected OutputStream getOutputStream(OutputStream result) throws IOException {
		
		return new KafkaLZ4BlockOutputStream(result);
	}
	
	public static void main(String []argc) throws IOException{
		
		Lz4Compressor gzip = new Lz4Compressor();
		gzip.compressRate();
		
	}


}
