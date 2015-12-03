package com.dianping.swallow.kafka.compress.algo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xerial.snappy.SnappyInputStream;
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

	@Override
	public String desc() {
		return "snappy";
	}

	@Override
	protected InputStream getInputStream(byte[] input) throws IOException {
		return new SnappyInputStream(new ByteArrayInputStream(input));
	}

}
