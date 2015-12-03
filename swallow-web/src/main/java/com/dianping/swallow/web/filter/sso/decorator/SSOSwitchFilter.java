package com.dianping.swallow.web.filter.sso.decorator;

import com.dianping.swallow.web.common.WebComponentConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Author   mingdongli
 * 15/12/2  下午8:46.
 */
public abstract class SSOSwitchFilter implements Filter{

    public static final String ADMINISTRATOR = "administrator";

    private boolean ssoEnable;

    private ServletContext context;

    private WebComponentConfig webComponentConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
        this.webComponentConfig = ctx.getBean(WebComponentConfig.class);
        ssoEnable = webComponentConfig.isSsoEnable();
        internalDoInit(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (ssoEnable) {
            internalDoFilter(request, response, chain);
        }else{
            if(request.getAttribute(ADMINISTRATOR) == null){
                request.setAttribute(ADMINISTRATOR, Boolean.TRUE);
            }
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

        internalDoDestroy();
    }

    protected abstract void internalDoInit(FilterConfig filterConfig) throws ServletException;

    protected abstract void internalDoFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

    protected abstract void internalDoDestroy();

}
