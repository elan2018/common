package com.elan.common.filter;

import com.alibaba.fastjson.JSON;
import com.elan.common.response.ResponseResultUtils;
import com.elan.common.utils.GenerationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class ReSubmitFilter implements Filter{
        private static Logger logger = LoggerFactory.getLogger(ReSubmitFilter.class);
        private List<String> excludes = new ArrayList<String>();

        private boolean isOpen = false;//是否开启该filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if(logger.isDebugEnabled()){
            logger.debug("resubmit filter init~~~~~~~~~~~~");
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String url =req.getRequestURI();
        if (url.endsWith("init_post")){
            resp.setContentType("text/plain;charset=UTF-8");
            resp.setCharacterEncoding("utf-8");
            String new_resubmit_token = GenerationUtil.uuid();
            String param = req.getParameter("path");
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.isNotEmpty(param)){
                param = param.replaceAll("/","_");
                String token_key =param+String.valueOf(param.hashCode());
                //更新resubmit_token
                req.getSession().setAttribute(token_key, new_resubmit_token);
                //将防止重复提交的key和token放入响应头
                resp.setHeader("resubmit_key",token_key);
                resp.setHeader(token_key, new_resubmit_token);
                goBack(resp,0,"初始成功");
            }else{
                goBack(resp,100,"初始resubmit_token错误，缺少path参数");
            }

            return ;
        }

        if(isOpen==false  || handleExcludeURL(req, resp) ){
            filterChain.doFilter(req, resp);
            return ;
        }
        if(logger.isDebugEnabled()){
            logger.debug("resubmit filter is running~~~~~~~~~~~~");
        }


        //不是post不进行处理
        if("post".equalsIgnoreCase(req.getMethod())==false){
            filterChain.doFilter(req, resp);
            return;
        }

        HttpSession session = req.getSession();

        //防止重复提交token的key

        String token_key_name = req.getHeader("resubmit_key");
        if(StringUtils.isEmpty(token_key_name)){
            token_key_name = req.getParameter("resubmit_key");
            if (StringUtils.isEmpty(token_key_name)){//获取resubmit失败
                goBack(resp,100,"无效的resubmit_key");
                return ;
            }
        }

        String request_resubmit_token = req.getHeader(token_key_name);
        if(StringUtils.isEmpty(request_resubmit_token)){
            request_resubmit_token = req.getParameter(token_key_name);
            if(StringUtils.isEmpty(request_resubmit_token)){
                goBack(resp,100,"无效的"+token_key_name);
                return ;
            }
        }

        Object resubmit_token = req.getSession().getAttribute(token_key_name);
        if (resubmit_token!=null ){//服务器redis中的token
            if (!request_resubmit_token.equals((String)resubmit_token)){
                goBack(resp,100,"无效的resubmit_token");
                return ;
            }
        }else{//服务器redis存储失败时
            goBack(resp,500,"服务器的resubmit_token获取失败");
            return ;
        }
        String new_resubmit_token =GenerationUtil.uuid();
        //更新resubmit_token
        req.getSession().setAttribute(token_key_name, new_resubmit_token);
        //将防止重复提交的key和token放入响应头
        resp.setHeader("resubmit_key",token_key_name);
        resp.setHeader(token_key_name, new_resubmit_token);
        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

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

    private void goBack(HttpServletResponse resp,int code,String message) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        ObjectMapper mapper = new ObjectMapper();
        resp.getWriter().print(mapper.writeValueAsString(ResponseResultUtils.error(code,message)));

    }
}
