package com.dianping.swallow.web.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.log.Log;

/**
 * @author mingdongli
 *
 *         2015年9月23日上午10:55:39
 */
public class LogFilter implements Filter {

	private static class ByteArrayServletStream extends ServletOutputStream {

		ByteArrayOutputStream baos;

		ByteArrayServletStream(ByteArrayOutputStream baos) {
			this.baos = baos;
		}

		public void write(int param) throws IOException {
			baos.write(param);
		}
	}

	private static class ByteArrayPrintWriter {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private PrintWriter pw = new PrintWriter(baos);

		private ServletOutputStream sos = new ByteArrayServletStream(baos);

		public PrintWriter getWriter() {
			return pw;
		}

		public ServletOutputStream getStream() {
			return sos;
		}

		byte[] toByteArray() {
			return baos.toByteArray();
		}
	}

	private class BufferedServletInputStream extends ServletInputStream {

		ByteArrayInputStream bais;

		public BufferedServletInputStream(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		public int available() {
			return bais.available();
		}

		public int read() {
			return bais.read();
		}

		public int read(byte[] buf, int off, int len) {
			return bais.read(buf, off, len);
		}

	}

	private class BufferedRequestWrapper extends HttpServletRequestWrapper {

		ByteArrayInputStream bais;

		ByteArrayOutputStream baos;

		BufferedServletInputStream bsis;

		byte[] buffer;

		public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
			super(req);
			InputStream is = req.getInputStream();
			baos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int letti;
			while ((letti = is.read(buf)) > 0) {
				baos.write(buf, 0, letti);
			}
			buffer = baos.toByteArray();
		}

		public ServletInputStream getInputStream() {
			try {
				bais = new ByteArrayInputStream(buffer);
				bsis = new BufferedServletInputStream(bais);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return bsis;
		}

		public byte[] getBuffer() {
			return buffer;
		}

	}

	private ServletContext context;

	private UserUtils extractUsernameUtils;

	private List<Pattern> excludePatterns = new LinkedList<Pattern>();

	private Set<String> includePatterns = new HashSet<String>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void init(FilterConfig fConfig) throws ServletException {

		this.context = fConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
		this.extractUsernameUtils = ctx.getBean(UserUtils.class);

		String excludeUrl = fConfig.getInitParameter("excludeURLs");
		String[] excludeUrls = excludeUrl.split(",");
		for (String exclude : excludeUrls) {
			if (exclude.contains("*")) {
				exclude = exclude.replaceAll("\\*", ".\\*");
			}
			Pattern excludePattern = Pattern.compile(exclude);
			excludePatterns.add(excludePattern);
		}

		String includeUrl = fConfig.getInitParameter("includeURLs");
		String[] includeUrls = includeUrl.split(",");
		includePatterns.addAll(Arrays.asList(includeUrls));

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		Log log = new Log();
		String username = null;
		String uri = null;

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		username = extractUsernameUtils.getUsername(httpRequest);
		uri = httpRequest.getRequestURI();
		BufferedRequestWrapper requestWrapper = new BufferedRequestWrapper(httpRequest);

		String requestContent = new String(requestWrapper.getBuffer());

		if (matchExcludePatterns(uri) || !matchIncludeUrl(uri)) {
			chain.doFilter(requestWrapper, response);
			return;
		}

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
		HttpServletResponse responseWrapper = new HttpServletResponseWrapper(httpResponse) {
			public PrintWriter getWriter() {
				return pw.getWriter();
			}

			public ServletOutputStream getOutputStream() {
				return pw.getStream();
			}

		};

		chain.doFilter(requestWrapper, responseWrapper);

		byte[] bytes = pw.toByteArray();
		try {
			httpResponse.getOutputStream().write(bytes);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error when getOutputStream of httpResponse", e);
			}
			bytes = e.getMessage().getBytes();
		}

		String result = new String(bytes);
		log.setCreateTime(new Date());
		if (StringUtils.isBlank(requestContent)) {
			HttpServletRequest req = (HttpServletRequest) request;
			@SuppressWarnings("unchecked")
			Map<String, String[]> param = req.getParameterMap();
			if (param != null) {
				requestContent = param.toString();
			}
		}
		log.setParameter(requestContent);
		log.setUrl(uri);
		log.setUser(username);
		log.setResult(result);

		logger.info(log.toString());

	}

	public void destroy() {
		// ignore
	}

	private boolean matchIncludeUrl(String uri) {

		for (String end : includePatterns) {
			if (uri.contains(end)) {
				return true;
			}
		}

		return false;
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

	public void setExtractUsernameUtils(UserUtils extractUsernameUtils) {
		this.extractUsernameUtils = extractUsernameUtils;
	}

}
