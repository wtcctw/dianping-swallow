package com.dianping.swallow.web.filter;

import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.filter.wrapper.BufferedRequestWrapper;
import com.dianping.swallow.web.filter.wrapper.ByteArrayPrintWriter;
import com.dianping.swallow.web.model.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mingdongli
 *         <p/>
 *         2015年9月23日上午10:55:39
 */
public class LogFilter implements Filter {

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

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        String username = extractUsernameUtils.getUsername(httpRequest);
        String uri = httpRequest.getRequestURI();
        String xforward = httpRequest.getHeader("X-Forwarded-For");
        String ip = xforward != null ? xforward : httpRequest.getRemoteAddr();
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
            StringBuilder stringBuilder = new StringBuilder();
            if (param != null) {
                for (Map.Entry<String, String[]> entry : param.entrySet()) {
                    stringBuilder.append(entry.getKey());
                    stringBuilder.append(" : ");
                    stringBuilder.append(entry.getValue()[0]);
                    stringBuilder.append(" ");
                }
                requestContent = stringBuilder.toString();
            }
        }
        log.setParameter(requestContent);
        log.setUrl(uri);
        log.setUser(username);
        log.setResult(result);
        log.setIp(ip);

        if (logger.isInfoEnabled()) {
            logger.info(log.toString());
        }

    }

    public void destroy() {
        // ignore
    }

    private boolean matchIncludeUrl(String uri) {

        for (String end : includePatterns) {
            if (uri.contains(end)) {
                int start = uri.indexOf(end);
                if (start != 0) {
                    if (uri.charAt(start - 1) == '/') {
                        if (start + end.length() == uri.length()) {
                            return true;
                        } else {
                            char c = uri.charAt(start + end.length());
                            if (c == '/' || c == '?') {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean matchExcludePatterns(String uri) {
        Iterator<Pattern> patternIter = excludePatterns.iterator();

        while (patternIter.hasNext()) {
            Pattern p = patternIter.next();
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
