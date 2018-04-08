package com.elan.common.filter;

import com.elan.common.response.ResponseResultUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSRF跨域请求伪造拦截
 * 除登录以外的post方法，都需要携带token，如果token为空或token错误，则返回异常提示
 * 注意在filter初始化参数内配置排除的url
 */
public class CsrfFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(CsrfFilter.class);

    public List<String> excludes = new ArrayList<String>();

    private boolean isOpen = false;//是否开启该filter

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(isOpen==false  || handleExcludeURL(req, resp) ){
            filterChain.doFilter(request, response);
            return ;
        }
        if(logger.isDebugEnabled()){
            logger.debug("csrf filter is running");
        }

        HttpSession session = req.getSession();
        Object token = session.getAttribute("token");
        if("post".equalsIgnoreCase(req.getMethod())==false){
            filterChain.doFilter(request, response);
            return;
        }

        String requestToken = req.getParameter("token");
        if(StringUtils.isBlank(requestToken) || requestToken.equals(token)==false){
            resp.setContentType("text/plain;charset=UTF-8");
             resp.setCharacterEncoding("utf-8");
            ObjectMapper mapper = new ObjectMapper();
            resp.getWriter().print(mapper.writeValueAsString(ResponseResultUtils.error(100,"无效的token")));
            return ;
        }
        filterChain.doFilter(request, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
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
    public void init(FilterConfig filterConfig) throws ServletException {
        if(logger.isDebugEnabled()){
            logger.debug("csrf filter init~~~~~~~~~~~~");
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

    @Override
    public void destroy() {}

}
