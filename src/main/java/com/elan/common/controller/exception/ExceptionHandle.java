package com.elan.common.controller.exception;

import com.elan.common.response.ResponseResult;
import com.elan.common.response.ResponseResultUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandle {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseResult Handle(HttpServletRequest req,Exception e)  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object>error = new HashMap<>();
        error.put("url",req.getQueryString() ==null ? req.getRequestURI() :req.getRequestURI()+"?"+req.getQueryString());
        error.put("method",req.getMethod());
        error.put("param",req.getParameterMap());
        error.put("error",e.getMessage());
        //将系统异常以打印出来
        try {
            logger.error("[系统异常]--"+mapper.writeValueAsString(error));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return ResponseResultUtils.error(1,"系统异常：",error);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public  ResponseResult handleUnexpectedServerError(HttpServletRequest req,RuntimeException e) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object>error = new HashMap<>();
        error.put("url",req.getQueryString() ==null ? req.getRequestURI() :req.getRequestURI()+"?"+req.getQueryString());
        error.put("method",req.getMethod());
        error.put("param",req.getParameterMap());
        error.put("error",e.getMessage());
        //将系统异常以打印出来
        try {
            logger.error("[运行期异常]--"+mapper.writeValueAsString(error));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return ResponseResultUtils.error(1,"运行期异常：",error);
    }

}
