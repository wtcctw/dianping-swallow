package com.dianping.swallow.web.filter.sso.decorator;

import org.jasig.cas.client.session.SingleSignOutFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/2  下午8:22.
 */
public class SingleSignOutFilterDecorator extends SSOSwitchFilter implements Filter {

    private SingleSignOutFilter singleSignOutFilter;

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

        singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.init(filterConfig);
    }

    @Override
    protected void internalDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        singleSignOutFilter.doFilter(request, response, chain);
    }

    @Override
    protected void internalDoDestroy() {

        singleSignOutFilter.destroy();
    }
}
