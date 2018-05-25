package com.elan.common.filter;

import com.elan.common.response.ResponseResultUtils;
import com.elan.common.utils.GenerationUtil;
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

    private List<String> excludes = new ArrayList<String>();

    private String test_csrf_token="";

    private boolean isOpen = false;//是否开启该filter

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(isOpen==false  || handleExcludeURL(req, resp) ){
            filterChain.doFilter(request, response);
            return ;
        }
        if(logger.isDebugEnabled()){
            logger.debug("csrf filter is running~~~~~~~~~~~~");
        }

        //不是post不进行处理
        if("post".equalsIgnoreCase(req.getMethod())==false || handleExcludeURL(req, resp)){
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession();
        //session中的csrf_token
        String csrf_key = "csrf_token";
        Object csrf_token = session.getAttribute(csrf_key);

        //在request参数或header中获取csrf_token
        String requestToken = req.getParameter("csrf_token");
        if(StringUtils.isEmpty(requestToken)){
            requestToken=req.getHeader("csrf_token");
        }

        if(StringUtils.isNoneBlank(requestToken) && requestToken.equals(test_csrf_token)){
            filterChain.doFilter(req, resp);
            return;
        }
        //不存在csrf_token，返回错误信息
        if(StringUtils.isBlank(requestToken) || requestToken.equals(csrf_token)==false){
            resp.setContentType("text/plain;charset=UTF-8");
            resp.setCharacterEncoding("utf-8");
            ObjectMapper mapper = new ObjectMapper();
            resp.getWriter().print(mapper.writeValueAsString(ResponseResultUtils.error(100,"无效的csrf_token")));
            return ;
        }

        filterChain.doFilter(req, resp);
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

        temp = filterConfig.getInitParameter("test-token");
        if(StringUtils.isNotBlank(temp)){
            test_csrf_token = temp;
        }
    }

    @Override
    public void destroy() {}

}
