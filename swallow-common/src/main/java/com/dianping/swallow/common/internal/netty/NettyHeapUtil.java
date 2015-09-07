package com.dianping.swallow.common.internal.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年9月6日 下午6:59:50
 */
public class NettyHeapUtil {
	
	protected static final Logger logger = LoggerFactory.getLogger(NettyHeapUtil.class);


	public static int directArenaCount(int expected, int pageSize, int maxOrder){
		
		int result = Math.min(expected, getMax(new DirectBuffPrinter().maxMemory(), pageSize, maxOrder));
		if(result != expected){
			if(logger.isInfoEnabled()){
				logger.info("[directArenaCount][expected vs real]" + expected + "," + result);
			}
		}
		return result;
	}

	public static int heapArenaCount(int expected, int pageSize, int maxOrder){
		
		int result = Math.min(expected, getMax(Runtime.getRuntime().maxMemory(), pageSize, maxOrder));
		if(result != expected){
			if(logger.isInfoEnabled()){
				logger.info("[heapArenaCount][expected vs real]" + expected + "," + result);
			}
		}
		return result;
	}

	private static int getMax(long maxMemory, int pageSize, int maxOrder) {
		
		maxMemory = maxMemory *2/3;
		return (int) (maxMemory/(pageSize << maxOrder) );
	}


	public static void main(String []argc){
		
		System.out.println( Runtime.getRuntime().maxMemory());
	}
	
}
