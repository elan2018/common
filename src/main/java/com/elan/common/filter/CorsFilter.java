package com.elan.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  可以不使用此过滤器，springmvc4已经提供更加强大的类,直接应用在类或方法上
 *  @CrossOrigin(origins = "http://domain2.com", maxAge = 3600)
 */
public class CorsFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    private List<String> originDomain=new ArrayList<>();
    private String method="";
    private String header="";
    private String age="0";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if(logger.isDebugEnabled()){
            logger.debug("cors filter init~~~~~~~~~~~~");
        }
        String origin = filterConfig.getInitParameter("origin");
        if (origin != null) {
            for(String domain : origin.split(",")){
                 originDomain.add(domain);
            }
        }

        method = filterConfig.getInitParameter("method");
        age = filterConfig.getInitParameter("age");
        header = filterConfig.getInitParameter("header");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String curDomain = request.getHeader("Origin");
        logger.debug(curDomain);
        if(originDomain.contains(curDomain)) {
            response.setHeader("Access-Control-Allow-Origin", curDomain);
            response.setHeader("Access-Control-Allow-Methods", method);
            response.setHeader("Access-Control-Max-Age", age);
            response.setHeader("Access-Control-Allow-Headers", header);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
