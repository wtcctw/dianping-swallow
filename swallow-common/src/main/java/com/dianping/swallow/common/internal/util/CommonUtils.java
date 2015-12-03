package com.dianping.swallow.common.internal.util;

/**
 * @author mengwenchao
 *
 * 2015年3月5日 下午6:09:19
 */
public class CommonUtils {
	
	public static final int DEFAULT_CPU_COUNT = 4;
	
	private static int REAL_CPU_COUNT = Runtime.getRuntime().availableProcessors();
	
	static {
		
		String realCpuCount = System.getProperty("REAL_CPU_COUNT"); 
		if( realCpuCount != null){
			REAL_CPU_COUNT = Integer.parseInt(realCpuCount);
		}
	}
	
	public static int getCpuCount(){
		if(REAL_CPU_COUNT <= 0 ){
			return DEFAULT_CPU_COUNT;
		}
		return REAL_CPU_COUNT;
	}

}
