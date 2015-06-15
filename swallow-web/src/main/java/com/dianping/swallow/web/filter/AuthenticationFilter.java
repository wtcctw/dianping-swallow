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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.SaveMessageController;
import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.AccessControlService;
import com.dianping.swallow.web.service.FilterMetaDataService;
import com.dianping.swallow.web.service.impl.AccessControlServiceImpl;
import com.dianping.swallow.web.service.impl.FilterMetaDataServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

import org.apache.commons.codec.binary.Base64;

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

			String uri = req.getRequestURI();

			this.context.log("Requested Resource::" + uri);

			String username = extractUsernameUtils.getUsername(req);
			boolean switchenv = filterMetaDataService.isShowContentToAll();
			if (switchenv && !uri.startsWith(ADMINURI)) {
				chain.doFilter(request, response);
			} else if (uri.startsWith(TOPICURI)) {
				if(StringUtils.isBlank(username)){
					String nameencode = req.getHeader(RetransmitFilter.AUTHORIZATION);
					if(StringUtils.isBlank(nameencode)){
						sendErrorMessage(response, ResponseStatus.E_NOAUTHENTICATION, ResponseStatus.M_NOAUTHENTICATION, false);
						return;
					}
					byte[] nameArray = Base64.decodeBase64(nameencode);     
					username = new String(nameArray); 
				}
				if(!accessControlService.checkVisitIsValid(username)) {
					this.context
							.log(String.format(
									"%s have no authenticaton to eidt topic",
									username));
					sendErrorMessage(response, ResponseStatus.E_UNAUTHENTICATION, ResponseStatus.M_UNAUTHENTICATION, false);
					return;
				}else{
					this.context.log(String.format(
							"%s have authenticaton to edit topic",
							username));
					chain.doFilter(request, response);
				}
			} else if (uri.startsWith(MESSAGEURI)) {
				String topicname = req.getParameter("topic");

				if (!accessControlService
						.checkVisitIsValid(username, topicname)) {
					this.context.log(String.format(
							"%s have no authenticaton to access %s", username,
							topicname));
					sendErrorMessage(response, ResponseStatus.E_UNAUTHENTICATION, ResponseStatus.M_UNAUTHENTICATION, true);
					return;
				} else {
					chain.doFilter(request, response);
				}
			} else if (uri.startsWith(ADMINURI)) {

				if (!accessControlService.checkVisitIsValid(username)) {
					this.context.log(String.format(
							"%s have no authenticaton to access admin memu",
							username));
					sendErrorMessage(response, ResponseStatus.E_UNAUTHENTICATION, ResponseStatus.M_UNAUTHENTICATION, false);
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
	
	private void sendErrorMessage(ServletResponse response, int status, String message, boolean send){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(SaveMessageController.STATUS, status);
		result.put(SaveMessageController.MESSAGE, message);
		if(send){
			result.put(SaveMessageController.SEND, 0);
		}
		JSONObject json=new JSONObject(result);
		response.setContentType("application/json");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			this.context.log("error when send response", e);
		}
		out.print(json);
		out.flush();
	}
	
}
