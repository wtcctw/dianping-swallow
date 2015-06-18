package com.dianping.swallow.web.util;

/**
 * @author mingdongli
 *
 *         2015年6月12日下午5:44:19
 */
public class ResponseStatus {

	public static final String M_IOEXCEPTION = "io exception"; // -8
	
	public static final String M_RUNTIMEEXCEPTION = "runtime exception"; // -7
	
	public static final String M_INTERRUPTEDEXCEPTION = "interrupted exception"; // -6
	
	public static final String M_PARSEEXCEPTION = "parse error"; // -5

	public static final String M_EMPTYCONTENT = "empty content"; // -4

	public static final String M_NOAUTHENTICATION = "no authenticaton"; // -3

	public static final String M_UNAUTHENTICATION = "unauthorized"; // -2

	public static final String M_MONGOWRITE = "write mongo error"; // -1

	public static final String M_SUCCESS = "success"; // 0

	public static final String M_TRY_MONGOWRITE = "read time out"; // 1

	// io exception
	public static final int E_IOEXCEPTION = -8;

	// runtime exception
	public static final int E_RUNTIMEEXCEPTION = -7;
	
	// interrupted exception
	public static final int E_INTERRUPTEDEXCEPTION = -6;
	
	// parse error
	public static final int E_PARSEEXCEPTION = -5;

	// empty content
	public static final int E_EMPTYCONTENT = -4;

	// http header中没有Authentication
	public static final int E_NOAUTHENTICATION = -3;

	// 没有权限
	public static final int E_UNAUTHENTICATION = -2;

	// mongo错误，不可重试
	public static final int E_MONGOWRITE = -1;
	// 操作成功
	public static final int SUCCESS = 0;

	// mongo错误，可重试
	public static final int E_TRY_MONGOWRITE = 1;

	public static final String[] M_ERRORTRYSTRING = { M_SUCCESS, M_TRY_MONGOWRITE };

	public static final String[] M_ERRORSTRING = { M_SUCCESS, M_TRY_MONGOWRITE,
			M_MONGOWRITE, M_UNAUTHENTICATION, M_NOAUTHENTICATION,
			M_EMPTYCONTENT, M_PARSEEXCEPTION, M_INTERRUPTEDEXCEPTION, M_RUNTIMEEXCEPTION, M_IOEXCEPTION };

}
