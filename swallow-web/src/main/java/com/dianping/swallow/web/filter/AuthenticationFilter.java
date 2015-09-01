package com.dianping.swallow.web.filter;

import java.io.IOException;
import java.io.InputStream;
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

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.common.internal.util.IOUtilsWrapper;
import com.dianping.swallow.web.controller.MessageRetransmitController;
import com.dianping.swallow.web.controller.utils.UserUtils;
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

	private UserUtils extractUsernameUtils;
	
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		ServletContext context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		this.authenticationService = ctx.getBean(AuthenticationServiceImpl.class);
		this.extractUsernameUtils = ctx.getBean(UserUtils.class);
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		String username = null;
		String uri = null;
		String topicname = null;
		ServletRequest requestWrapper = null;  
        if(request instanceof HttpServletRequest) {  
        	HttpServletRequest req = (HttpServletRequest) request;
        	username = extractUsernameUtils.getUsername(req);
        	uri = req.getRequestURI();
            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);  
        }  
        if(null == requestWrapper) {  
            chain.doFilter(request, response);  
        } else {
        	
    		InputStream inputStream = requestWrapper.getInputStream();
			String requestContent = IOUtilsWrapper.convetStringFromRequest(inputStream);
			try {
				JSONObject json = new JSONObject(requestContent);
				topicname = json.getString("topic");
			} catch (JSONException e) {
				HttpServletRequest req = (HttpServletRequest) request;
				topicname = req.getParameter("topic");
			}

    		boolean isPassed = authenticationService.isValid(username, topicname, uri);

    		if (isPassed) {
    			chain.doFilter(requestWrapper, response);  
    		} else {
    			sendErrorMessage(response, ResponseStatus.UNAUTHENTICATION);
    			return;
    		}
        }  
		
	}

	public void destroy() {
		// ignore
	}

	private void sendErrorMessage(ServletResponse response, ResponseStatus rs)
			throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(MessageRetransmitController.STATUS, rs.getStatus() );
		result.put(MessageRetransmitController.MESSAGE, rs.getMessage());

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