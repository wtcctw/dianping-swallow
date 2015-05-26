package com.dianping.swallow.common.internal.util;

/**
 * @author mengwenchao
 *
 * 2015年3月5日 下午6:09:19
 */
public class CommonUtils {
	
	public static final int DEFAULT_CPU_COUNT = 4;
	
	public static int getCpuCount(){
		int cpuCount = Runtime.getRuntime().availableProcessors();
		if(cpuCount <= 0 ){
			cpuCount = 4;
		}
		return cpuCount;
	}

}
