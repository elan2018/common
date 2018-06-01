package com.elan.common.controller.exception;

import com.elan.common.response.ResponseResult;
import com.elan.common.response.ResponseResultUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("${server.error.path:/error}")
public class MyErrorController  implements ErrorController  {
    private Logger logger = LoggerFactory.getLogger(MyErrorController.class);

    @Value("${server.error.path:/error}")
    private String errorPath;

    @Value("${server.error.trace:true}")
    private boolean isProduction;

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(produces = {"text/html"})
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> errorAttributes = getErrorAttributes(request, !this.isProduction);
        Integer status=(Integer)errorAttributes.get("status");
        String path=(String)errorAttributes.get("path");
        String messageFound=(String)errorAttributes.get("message");
        String message="";
        String trace ="";
        if(!StringUtils.isEmpty(path)){
            message=String.format("Requested path %s with result %s",path,messageFound);
        }
        if(!this.isProduction) {
            trace = (String) errorAttributes.get("trace");
            if(!StringUtils.isEmpty(trace)) {
                message += String.format(" and trace %s", trace);
            }
        }
        Map<String,Object> model = new HashMap<>();
        model.put("status",status);
        model.put("path",path);
        model.put("message",message);
        ModelAndView modelAndView = new ModelAndView();
        logger.info(errorPath);
        logger.info(message);
        modelAndView.setViewName(errorPath);
        modelAndView.addObject("error",model);
        return modelAndView;
    }

    @RequestMapping(headers = {"X-Requested-With"},produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseResult error(HttpServletRequest request, HttpServletResponse response) {
        return buildBody(request, !this.isProduction);

    }

    private ResponseResult buildBody(HttpServletRequest request,Boolean includeStackTrace){
        Map<String,Object> errorAttributes = getErrorAttributes(request, includeStackTrace);
        Integer status=(Integer)errorAttributes.get("status");
        String path=(String)errorAttributes.get("path");
        String messageFound=(String)errorAttributes.get("message");
        String message="";
        String trace ="";
        if(!StringUtils.isEmpty(path)){
            message=String.format("Requested path %s with result %s",path,messageFound);
        }
        if(includeStackTrace) {
            trace = (String) errorAttributes.get("trace");
            if(!StringUtils.isEmpty(trace)) {
                message += String.format(" and trace %s", trace);
            }
        }
        return new ResponseResult(status,message);
    }

    @Override
    public String getErrorPath() {
        return errorPath;
    }
    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }

}
