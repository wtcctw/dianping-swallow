package com.dianping.swallow.kafka.compress;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.kafka.AbstractKafkaTest;
import com.dianping.swallow.kafka.compress.algo.Compressor;
import com.dianping.swallow.kafka.compress.algo.GzipCompressor;
import com.dianping.swallow.kafka.compress.algo.Lz4Compressor;
import com.dianping.swallow.kafka.compress.algo.SnappyCompressor;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:07:05
 */
public class CompressTest extends AbstractKafkaTest{
	
	private Compressor[] compressors = new Compressor[]{new Lz4Compressor(), new GzipCompressor(), new SnappyCompressor()};

	private String random = randomMessage(1 << 20);
//	private String random = commonString(50);
	private byte []input = random.getBytes();

	
	@Test
	public void testRatio(){
		
		for(Compressor compressor : compressors){
			byte []out = compressor.compress(input);
			
			int i;
			for(i = out.length - 1 ;i >=0 ; i --){
				if(out[i] != 0){
					break;
				}
			}
			if(logger.isInfoEnabled()){
				logger.info("[testRatio]" + compressor.desc() + ":" + (double)input.length/(i + 1));
			}
		}
	}
	
	@Test
	public void testTime(){
		
		int compressCount = 100;
		for(Compressor compressor : compressors){
			
			long begin = System.currentTimeMillis();
			for(int i=0; i < compressCount ; i++){
				byte []out = compressor.compress(input);
				byte []result = compressor.decompress(out);
			}
			long end = System.currentTimeMillis();
			if(logger.isInfoEnabled()){
				logger.info("[testRatio][time used]" + compressor.desc() + ":" + (end - begin));
			}
		}
	}
	
	@Test
	public void testFunction(){
		
		for(Compressor compressor : compressors){
			
			if(logger.isInfoEnabled()){
				logger.info("[testFunction]" + compressor.desc());
			}
			byte[]out = compressor.compress(input);
			byte[]result = compressor.decompress(out);
			
			String resultStr = new String(result);
			
			if(logger.isInfoEnabled()){
				logger.info(resultStr);
			}
			Assert.assertArrayEquals(input, result);
		}
	}

}
