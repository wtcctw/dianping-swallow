package com.dianping.swallow.web.filter.sso.decorator;

import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/2  下午8:51.
 */
public class HttpServletRequestWrapperFilterDecorator extends SSOSwitchFilter implements Filter {

    private HttpServletRequestWrapperFilter httpServletRequestWrapperFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {

        super.destroy();
    }

    @Override
    protected void internalDoInit(FilterConfig filterConfig) throws ServletException {

        httpServletRequestWrapperFilter = new HttpServletRequestWrapperFilter();
        httpServletRequestWrapperFilter.init(filterConfig);
    }

    @Override
    protected void internalDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        httpServletRequestWrapperFilter.doFilter(request, response, chain);
    }

    @Override
    protected void internalDoDestroy() {

        httpServletRequestWrapperFilter.destroy();
    }
}
