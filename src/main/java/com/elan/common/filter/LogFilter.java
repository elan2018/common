package com.elan.common.filter;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFilter implements Filter {
    private static  Logger logger = LoggerFactory.getLogger(LogFilter.class);

    private List<String> excludes = new ArrayList<String>();

    private boolean isOpen = false;//是否开启该filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if(logger.isDebugEnabled()){
            logger.debug("Log filter init ====================");
        }
        String temp = filterConfig.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i]);
            }
        }

        temp = filterConfig.getInitParameter("isOpen");
        if(StringUtils.isNotBlank(temp) && "true".equals(temp)){
            isOpen = true;
        }

    }
    private boolean handleExcludeURL(HttpServletRequest request) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        String url = request.getServletPath();
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isOpen){
            HttpServletRequest request=(HttpServletRequest)servletRequest;
            if(handleExcludeURL(request)) {
                String url = request.getRequestURI();
                String param = request.getQueryString();
                String method = request.getMethod();
                if (method.equalsIgnoreCase("post")) {
                    Map<String, String[]> params = request.getParameterMap();
                    Set keys = params.keySet();
                }
                logger.info("url=" + url,"method="+method, "query=" + param);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
