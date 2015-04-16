package com.dianping.swallow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年4月2日 下午6:24:25
 */
public abstract class AbstractController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();	
	
}
