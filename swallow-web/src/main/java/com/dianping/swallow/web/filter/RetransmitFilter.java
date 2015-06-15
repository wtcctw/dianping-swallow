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
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.codehaus.jettison.json.JSONObject;

import com.dianping.swallow.web.controller.SaveMessageController;
import com.dianping.swallow.web.task.RandomStringGenerator;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年6月10日上午10:58:19
 */
public class RetransmitFilter implements Filter {

	public static final String AUTHORIZATION = "Authorization";

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
		String randomString = req.getHeader(AUTHORIZATION);
		if (StringUtils.isEmpty(randomString)) {
			chain.doFilter(request, response); // 没有AUTHORIZATION,正常通过web端访问
		} else if (randomString
				.equals(randomStringGenerator.loadRandomString())) {
			request.setAttribute("skipfilter", true);
			chain.doFilter(request, response);
		} else {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(SaveMessageController.STATUS, ResponseStatus.E_UNAUTHENTICATION);
			result.put(SaveMessageController.SEND, 0);
			result.put(SaveMessageController.MESSAGE, ResponseStatus.M_UNAUTHENTICATION);
			this.context
					.log(String
							.format("Authentication String %s out of date! Please contact operation to get right Authentication key",
									randomString));
			JSONObject json=new JSONObject(result);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(json);
			out.flush();
			return;
		}
	}

	@Override
	public void destroy() {
		// ignore

	}

}
