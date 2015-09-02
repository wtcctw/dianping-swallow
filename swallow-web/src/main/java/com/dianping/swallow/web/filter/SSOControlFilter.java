package com.dianping.swallow.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import jodd.util.StringUtil;

/**
 * @author mingdongli
 *
 *         2015年8月28日上午9:28:00
 */
public class SSOControlFilter implements Filter {

	private String ssoenable;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		ssoenable = filterConfig.getInitParameter("ssoenable");

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		if ("false".equals(ssoenable)) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String path = httpServletRequest.getServletPath();
			String info = httpServletRequest.getPathInfo();
			String url = null;

			if (StringUtil.isNotBlank(info)) {
				url = path + info;
			} else {
				url = path;
			}
			request.setAttribute("administrator", Boolean.TRUE);
			request.getRequestDispatcher(url).forward(request, response);
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
