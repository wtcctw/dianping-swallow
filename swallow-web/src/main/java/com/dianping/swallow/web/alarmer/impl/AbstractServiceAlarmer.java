package com.dianping.swallow.web.alarmer.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.alarm.EventReporter;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午6:06:14
 */
public abstract class AbstractServiceAlarmer extends AbstractAlarmer {
	
	protected Map<String,Boolean> lastCheckStatus = new HashMap<String,Boolean>();
	
	@Autowired
	protected EventReporter eventReporter;

}
