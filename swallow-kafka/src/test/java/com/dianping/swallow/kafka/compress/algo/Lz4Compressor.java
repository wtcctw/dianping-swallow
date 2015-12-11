package com.dianping.swallow.kafka.compress.algo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.kafka.common.message.KafkaLZ4BlockInputStream;
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
	
	protected InputStream getInputStream(byte [] input) throws IOException{
		return new KafkaLZ4BlockInputStream(new ByteArrayInputStream(input));
	}

	@Override
	public String desc() {
		return "lz4";
	}


}
