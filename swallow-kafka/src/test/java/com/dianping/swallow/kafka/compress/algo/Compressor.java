package com.dianping.swallow.kafka.compress.algo;

/**
 * @author mengwenchao
 *
 * 2015年12月3日 上午10:34:00
 */
public interface Compressor {
	
	byte [] compress(byte []input);
	
	byte [] decompress(byte []input);

	String desc();
}
