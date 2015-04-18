package com.dianping.swallow.common.internal.util;

import com.dianping.lion.EnvZooKeeperConfig;

/**
 * @author mengwenchao
 *
 * 2015年4月18日 下午4:54:27
 */
public class EnvUtil {
	
	private static final String basicWebAddress = "swallow.dp";
	
	private static final String env;
	
	static{
		env = EnvZooKeeperConfig.getEnv();
	}
	
	public static boolean isDev(){
		
		return env.equals("dev");
	}
	
	public static boolean isAlpha(){
		
		return env.equals("alpha");
	}

	public static boolean isQa(){
		
		return env.equals("qa");
	}

	public static boolean isPpe(){
		
		return env.equals("prelease");
	}

	public static boolean isProduct(){
		
		return env.equals("product");
	}
	
	public static String getWebAddress(){
		
		if(!isProduct()){
			if(!isQa()){
				return "http://" + env + "." + basicWebAddress; 
			}
			return "http://beta." + basicWebAddress;
		}
		
		return "http://" + basicWebAddress;
		
	}

}
