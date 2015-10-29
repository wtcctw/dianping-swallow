package com.dianping.swallow.web.filter;

import java.io.IOException;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.web.Log4jWebSupport;
import org.apache.logging.log4j.web.WebLoggerContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.filter.wrapper.BufferedRequestWrapper;
import com.dianping.swallow.web.filter.wrapper.ByteArrayPrintWriter;
import com.dianping.swallow.web.model.log.Log;

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

    private Logger logger = LogManager.getLogger(LogFilter.class);

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
            if (param != null) {
                StringBuilder content = new StringBuilder(" {");
                for(Map.Entry<String, String[]> entry : param.entrySet()){
                    String[] value = entry.getValue();
                    content.append(entry.getKey()).append(" = ").append(StringUtils.join(value, ",")).append(" ,");
                }
                int length = content.length();
                requestContent = content.substring(0, length - 2) + " }";
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
