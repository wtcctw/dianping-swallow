package com.dianping.swallow.web.filter.sso.decorator;

import org.jasig.cas.client.validation.Saml11TicketValidationFilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/2  下午8:44.
 */
public class Saml11TicketValidationFilterDecorator extends SSOSwitchFilter implements Filter {

    private Saml11TicketValidationFilter saml11TicketValidationFilter;

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

        saml11TicketValidationFilter = new Saml11TicketValidationFilter();
        saml11TicketValidationFilter.init(filterConfig);
    }

    @Override
    protected void internalDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        saml11TicketValidationFilter.doFilter(request, response, chain);
    }

    @Override
    protected void internalDoDestroy() {

        saml11TicketValidationFilter.destroy();
    }
}
