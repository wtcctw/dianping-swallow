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

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.AccessControlService;
import com.dianping.swallow.web.service.FilterMetaDataService;
import com.dianping.swallow.web.service.impl.AccessControlServiceImpl;
import com.dianping.swallow.web.service.impl.FilterMetaDataServiceImpl;

/**
 * @author mingdongli
 *
 *         2015年5月25日下午5:52:13
 */
public class AuthenticationFilter implements Filter {

	private static final String TOPICURI = "/console/topic/auth";

	private static final String MESSAGEURI = "/console/message/auth";

	private static final String ADMINURI = "/console/admin/auth";

	private ServletContext context;

	private AccessControlService accessControlService;

	private ExtractUsernameUtils extractUsernameUtils;

	private FilterMetaDataService filterMetaDataService;

	public void init(FilterConfig fConfig) throws ServletException {
		this.context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.context);
		this.accessControlService = ctx.getBean(AccessControlServiceImpl.class);
		this.filterMetaDataService = ctx
				.getBean(FilterMetaDataServiceImpl.class);
		this.extractUsernameUtils = ctx.getBean(ExtractUsernameUtils.class);
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request.getAttribute("skipfilter") != null) {
			chain.doFilter(request, response);
		} else {

			HttpServletRequest req = (HttpServletRequest) request;

			HttpServletResponse res = (HttpServletResponse) response;

			String uri = req.getRequestURI();

			this.context.log("Requested Resource::" + uri);

			String username = extractUsernameUtils.getUsername(req);
			boolean switchenv = filterMetaDataService.isShowContentToAll();
			if (switchenv) {
				chain.doFilter(request, response);
			} else if (uri.startsWith(TOPICURI) || uri.startsWith(MESSAGEURI)) {
				String topicname = req.getParameter("topic");

				if (!accessControlService
						.checkVisitIsValid(username, topicname)) {
					this.context.log(String.format(
							"%s have no authenticaton to access %s", username,
							topicname));
					res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
					return;
				} else {
					chain.doFilter(request, response);
				}
			} else if (uri.startsWith(ADMINURI)) {

				if (!accessControlService.checkVisitIsValid(username)) {
					this.context.log(String.format(
							"%s have no authenticaton to access admin memu",
							username));
					res.sendError(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
					return;
				} else {
					chain.doFilter(request, response);
				}
			} else {
				// pass the request along the filter chain
				chain.doFilter(request, response);
			}
		}

	}

	public void destroy() {
		// close any resources here
	}

}
