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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.task.RandomStringGenerator;

/**
 * @author mingdongli
 *
 *         2015年6月10日上午10:58:19
 */
public class RetransmitFilter implements Filter {

	private static final String AUTHORIZATION = "Authorization";

	private ServletContext context;

	private RandomStringGenerator randomStringGenerator;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.context = filterConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.context);
		this.randomStringGenerator = ctx.getBean(RandomStringGenerator.class);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String randomString = req.getHeader(AUTHORIZATION);
		if (StringUtils.isEmpty(randomString)) {
			chain.doFilter(request, response); // 没有AUTHORIZATION,正常通过web端访问
		} else if (randomString
				.equals(randomStringGenerator.loadRandomString())) {
			request.setAttribute("skipfilter", true);
			chain.doFilter(request, response);
		} else {
			this.context
					.log(String
							.format("Authentication String %s out of time! Please contact operation to get right Authentication key",
									randomString));
			res.sendError(
					javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
					String.format(
							"Authentication string %s is out of time, please contact operator to get latest string.",
							randomString));
			return;
		}
	}

	@Override
	public void destroy() {
		// ignore

	}

}
