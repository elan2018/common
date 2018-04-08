package com.elan.common.controller.exception;

import com.elan.common.response.ResponseResult;
import com.elan.common.response.ResponseResultUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class MyErrorController implements ErrorController {
    private final static Logger logger = LoggerFactory.getLogger(MyErrorController.class);
     @Override
        public String getErrorPath() {
            return "/error";
        }

        @RequestMapping
        @ResponseBody
        public ResponseResult doHandleError(HttpServletRequest req,Exception e)  {
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object>error = new HashMap<>();
            error.put("url",req.getQueryString() ==null ? req.getRequestURI() :req.getRequestURI()+"?"+req.getQueryString());
            error.put("method",req.getMethod());
            error.put("param",req.getParameterMap());
            String msg = (String)req.getAttribute("javax.servlet.error.message");
            Integer code =(Integer)req.getAttribute("javax.servlet.error.status_code");
           //String uri=(String) req.getAttribute("javax.servlet.forward.request_uri");
           String error_uri=(String)req.getAttribute("javax.servlet.error.request_uri");
            error.put("original_url",req.getQueryString() ==null ? error_uri :error_uri+"?"+req.getQueryString());
            //将系统异常以打印出来
            try {
                logger.error("[错误]--"+mapper.writeValueAsString(error));
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
            return ResponseResultUtils.error(code,msg,error);
        }

}
