package com.dianping.swallow.web.filter.sso.decorator;

import org.jasig.cas.client.authentication.Saml11AuthenticationFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/2  下午8:25.
 */
public class Saml11AuthenticationFilterDecorator extends SSOSwitchFilter implements Filter {

    private Saml11AuthenticationFilter saml11AuthenticationFilter;

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

        saml11AuthenticationFilter = new Saml11AuthenticationFilter();
        saml11AuthenticationFilter.init(filterConfig);
    }

    @Override
    protected void internalDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        saml11AuthenticationFilter.doFilter(request, response, chain);
    }

    @Override
    protected void internalDoDestroy() {

        saml11AuthenticationFilter.destroy();
    }


}
