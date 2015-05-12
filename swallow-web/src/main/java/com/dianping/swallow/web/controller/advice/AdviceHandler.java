package com.dianping.swallow.web.controller.advice;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author mengwenchao
 *
 * 2015年4月30日 上午10:40:41
 */
@ControllerAdvice()
public class AdviceHandler {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler
	public void handleException(HttpServletRequest request, Exception ex){
		logger.error("[handleException][]" + request.getRequestURI(), ex);
		
	}
}
