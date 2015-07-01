package com.dianping.swallow.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.MessageRetransmitController;
import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.impl.AuthenticationServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年5月25日下午5:52:13
 */
public class AuthenticationFilter implements Filter {

	private AuthenticationService authenticationService;

	private ExtractUsernameUtils extractUsernameUtils;

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		ServletContext context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		this.authenticationService = ctx.getBean(AuthenticationServiceImpl.class);
		this.extractUsernameUtils = ctx.getBean(ExtractUsernameUtils.class);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		String username = extractUsernameUtils.getUsername(req);
		String topicname = req.getParameter("topic");

		boolean isPassed = authenticationService.isValid(username, topicname, uri);

		if (isPassed) {
			chain.doFilter(request, response);
		} else {
			sendErrorMessage(response, ResponseStatus.UNAUTHENTICATION.getStatus(),
					ResponseStatus.UNAUTHENTICATION.getMessage(), false);
			return;
		}

	}

	public void destroy() {
		// ignore
	}

	private void sendErrorMessage(ServletResponse response, int status, String message, boolean needSend)
			throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MessageRetransmitController.STATUS, status);
		result.put(MessageRetransmitController.MESSAGE, message);
		if (needSend) {
			result.put(MessageRetransmitController.SEND, 0);
		}

		JSONObject json = new JSONObject(result);

		response.setContentType("application/json");

		PrintWriter out = response.getWriter();

		try {
			out.print(json);
			out.flush();
		} finally {
			out.close();
		}
	}

}
