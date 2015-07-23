package com.dianping.swallow.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;


/**
 * @author mingdongli
 *
 * 2015年7月23日下午3:10:53
 */
public class LogFilter implements Filter {

	private ServletContext context;

	private ExtractUsernameUtils extractUsernameUtils;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void init(FilterConfig fConfig) throws ServletException {
		
		this.context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
		this.extractUsernameUtils = ctx.getBean(ExtractUsernameUtils.class);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String uri = req.getRequestURI();
		String username = extractUsernameUtils.getUsername(req);

		logger.info(String.format("%s request %s", username, uri));

		chain.doFilter(request, response);

	}

	public void destroy() {
		// ignore
	}

}
