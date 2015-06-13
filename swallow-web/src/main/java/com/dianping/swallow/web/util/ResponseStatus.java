package com.dianping.swallow.web.util;


/**
 * @author mingdongli
 *
 * 2015年6月12日下午5:44:19
 */
public class ResponseStatus {

	public static final String M_MONGOWRITE = "write mongo error";  //-3
	
	public static final String M_NOAUTHENTICATION = "no authenticaton in http header";  //-2
	
	public static final String M_UNAUTHENTICATION = "have no authenticaton";  //-1

	public static final String M_SUCCESS = "success";  //0

	public static final String M_TRY_MONGOWRITE = "write mongo error to retry";  //1
	
	public static final String M_TRY_EMPTYCONTENT = "empty content";  //2

	//mongo错误，不可重试
	public static final int E_MONGOWRITE = -3;
	
	//http header中没有Authentication
	public static final int E_NOTHENTICATION = -2;
	
	//没有权限
	public static final int E_UNTHENTICATION = -1;
	
	//操作成功
	public static final int SUCCESS = 0;
	
	//mongo错误，可重试
	public static final int E_TRY_MONGOWRITE = 1;

	public static final int E_TRY_EMPTYCONTENT = 2;


}
