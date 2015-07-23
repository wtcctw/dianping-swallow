package com.dianping.swallow.web.alarm.impl;

import java.util.HashMap;
import java.util.Map;


/**
*
* @author qiyin
*
*/
public abstract class AbstractServiceAlarmFilter extends AbstractAlarmFilter {
	
	protected Map<String,Boolean> lastCheckStatus = new HashMap<String,Boolean>();
	
}
