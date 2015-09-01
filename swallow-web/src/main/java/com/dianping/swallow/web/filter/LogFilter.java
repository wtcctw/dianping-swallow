package com.dianping.swallow.web.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.dianping.swallow.web.controller.utils.UserUtils;

/**
 * @author mingdongli
 *
 *         2015年7月23日下午3:10:53
 */
public class LogFilter implements Filter {

	private ServletContext context;

	private UserUtils extractUsernameUtils;

	private List<Pattern> excludePatterns = new LinkedList<Pattern>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void init(FilterConfig fConfig) throws ServletException {

		this.context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
		this.extractUsernameUtils = ctx.getBean(UserUtils.class);
		String excludeUrl = fConfig.getInitParameter("excludeURLs");
		String[] excludeUrls = excludeUrl.split(",");
		for (String exclude : excludeUrls) {
			if(exclude.contains("*")){
				exclude = exclude.replaceAll("\\*", ".\\*");
			}
			Pattern excludePattern = Pattern.compile(exclude);
			excludePatterns.add(excludePattern);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String uri = req.getRequestURI();
		String username = extractUsernameUtils.getUsername(req);
		@SuppressWarnings("unchecked")
		Map<String, String[]> param = req.getParameterMap();
		String addr = req.getRemoteAddr();

		if (matchExcludePatterns(uri)) {
			chain.doFilter(request, response);
			return;
		}
		logger.info(String.format("%s request %s with parameter %s from %s", username, uri, param.toString(), addr));

		chain.doFilter(request, response);

	}

	public void destroy() {
		// ignore
	}

	private boolean matchExcludePatterns(String uri) {
		Iterator<Pattern> patternIter = excludePatterns.iterator();

		while (patternIter.hasNext()) {
			Pattern p = (Pattern) patternIter.next();
			Matcher m = p.matcher(uri);
			if (m.matches()) {
				return true;
			}
		}

		return false;
	}

}
