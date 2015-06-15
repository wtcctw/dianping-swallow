package com.dianping.swallow.web.util;


/**
 * @author mingdongli
 *
 * 2015年6月12日下午5:44:19
 */
public class ResponseStatus {
	
	public static final String M_EMPTYCONTENT = "empty content";  //-4
	
	public static final String M_NOAUTHENTICATION = "no authenticaton";  //-3
	
	public static final String M_UNAUTHENTICATION = "unauthorized";  //-2

	public static final String M_MONGOWRITE = "write mongo error";  //-1

	public static final String M_SUCCESS = "success";  //0

	public static final String M_TRY_MONGOWRITE = "read time out";  //1
	
	
	//empty content
	public static final int E_EMPTYCONTENT = -4;
	
	//http header中没有Authentication
	public static final int E_NOAUTHENTICATION = -3;
	
	//没有权限
	public static final int E_UNAUTHENTICATION = -2;
	
	//mongo错误，不可重试
	public static final int E_MONGOWRITE = -1;
	//操作成功
	public static final int SUCCESS = 0;
	
	//mongo错误，可重试
	public static final int E_TRY_MONGOWRITE = 1;

}
