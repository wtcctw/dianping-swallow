package com.dianping.swallow.web.filter;

import com.dianping.swallow.web.common.WebComponentConfig;
import jodd.util.StringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author mingdongli
 *
 *         2015年8月28日上午9:28:00
 */
public class SSOControlFilter implements Filter {

	private ServletContext context;

	private boolean ssoenable;

	private WebComponentConfig webComponentConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.context = filterConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
		this.webComponentConfig = ctx.getBean(WebComponentConfig.class);
		ssoenable = webComponentConfig.isSsoEnable();

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		if (!ssoenable) {
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
