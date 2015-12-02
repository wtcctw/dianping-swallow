package com.dianping.swallow.kafka.compress.algo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:10:07
 */
public abstract class AbstractCompressor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractCompressor() {
		
	}
	
	
	
	public String randomMessage(int length){
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i < length;i++){
			int random = (int) ((double)26 * Math.random());
			sb.append((char)('a' + random));
		}
		
		return sb.toString();
		
	}
	
	public void compressRate() throws IOException{
		
		int messageSize = 1 << 20;
		
		String message = randomMessage(messageSize);
		byte[] bytes = message.getBytes();
		
		OutputStream ous = null;
		try{
			ByteArrayOutputStream baous = new ByteArrayOutputStream();
			ous = getOutputStream(baous);
			ous.write(bytes);
			byte []result  = baous.toByteArray();

			int i = result.length - 1;
			for( ; i >= 0; i--){
				if(result[i] != 0){
					break;
				}
			}
			i++;
			
			if(logger.isInfoEnabled()){
				logger.info("[compressRate]" + (double)bytes.length/result.length);
			}
			
		}finally{
			if(ous != null){
				ous.close();
			}
		}
	}
	
	



	protected abstract OutputStream getOutputStream(OutputStream result) throws IOException;
}
