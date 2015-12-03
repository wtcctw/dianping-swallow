package com.dianping.swallow.kafka.compress.algo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年11月25日 下午4:10:07
 */
public abstract class AbstractCompressor implements Compressor{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractCompressor() {
		
	}
	
	
	public byte [] decompress(byte []input){
		
		try {
			DataInputStream dis = new DataInputStream(getInputStream(input));
			byte []result = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(true){
				int len = dis.read(result);
				if(len == -1 ){
					break;
				}
				baos.write(result, 0, len);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			logger.error("[decompress]", e);
		}
		
		return null;
	}

	
	public byte[] compress(byte[] input) {

		OutputStream ous = null;
		try{
			ByteArrayOutputStream baous = new ByteArrayOutputStream();
			ous = getOutputStream(baous);
			ous.write(input);
			ous.flush();
			ous.close();
			byte []result  = baous.toByteArray();
			return result;

		} catch (IOException e) {
			logger.error("[compress]", e);
		}finally{
			if(ous != null){
				try {
					ous.close();
				} catch (IOException e) {
					logger.error("[compress]", e);
				}
			}
		}
		return null;
	}
	
	protected abstract OutputStream getOutputStream(OutputStream result) throws IOException;
	
	protected abstract InputStream getInputStream(byte []input) throws IOException;

}
