package com.elan.common.utils;

import com.elan.common.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP请求访问工具
 */
public class RestTemplateUtil {
    private Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

    private RestTemplate restTemplate;
    public RestTemplateUtil(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public  <T> T  post(String url, Map<String, String> headers, Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.POST,headers ,params,responseType);
        return rss.getBody();
    }
    public  ResponseResult  post(String url, Map<String, String> headers, Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.POST,headers ,params,ResponseResult.class);
        return rss.getBody();
    }

    public <T> T  post(String url,Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.POST,params,responseType);
        return rss.getBody();
    }
    public ResponseResult  post(String url,Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.POST,params,ResponseResult.class);
        return rss.getBody();
    }


    public <T> T  get(String url, Map<String, String> headers,Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.GET,headers,params,responseType);
        return rss.getBody();
    }
    public ResponseResult  get(String url, Map<String, String> headers,Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.GET,headers,params,ResponseResult.class);
        return rss.getBody();
    }
    public <T> T  get(String url, Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.GET,params,responseType);
        return rss.getBody();
    }
    public ResponseResult get(String url, Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.GET,params,ResponseResult.class);
        return rss.getBody();
    }

    public <T> T  delete(String url, Map<String, String> headers,Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.DELETE, headers,params,responseType);
        return rss.getBody();
    }
    public ResponseResult  delete(String url, Map<String, String> headers,Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.DELETE, headers,params,ResponseResult.class);
        return rss.getBody();
    }
    public <T> T  delete( String url, Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request(url, HttpMethod.DELETE,params,responseType);
        return rss.getBody();
    }
    public ResponseResult  delete( String url, Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request(url, HttpMethod.DELETE,params,ResponseResult.class);
        return rss.getBody();
    }

    public <T> T  put(String url, Map<String, String> headers,Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request( url, HttpMethod.PUT, headers,params,responseType);
        return rss.getBody();
    }
    public ResponseResult  put(String url, Map<String, String> headers,Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request( url, HttpMethod.PUT, headers,params,ResponseResult.class);
        return rss.getBody();
    }
    public <T> T  put(String url, Map<String, ?> params,Class<T> responseType) {
        ResponseEntity<T> rss = request( url, HttpMethod.PUT, params,responseType);
        return rss.getBody();
    }
    public ResponseResult put(String url, Map<String, ?> params) {
        ResponseEntity<ResponseResult> rss = request( url, HttpMethod.PUT, params,ResponseResult.class);
        return rss.getBody();
    }

    /**
     * @param url
     * @param method
     * @param params maybe null
     * @return
     */
    private <T> ResponseEntity<T> request(String url, HttpMethod method, Map<String, String> headers,Map<String, ?> params,Class<T> responseType) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Assert.hasText(url,"URL不允许为空！");
        Assert.notNull(responseType,"输出对象类型不允许空！");
        //获取header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            requestHeaders.add(key, value);
        }
        if (headers!=null && headers.size()>0){

            Iterator<String> iter = headers.keySet().iterator();
            while (iter.hasNext()) {
               String  key = iter.next();
                requestHeaders.add(key,(String)headers.get(key));

            }
        }
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        //获取parameter信息

        String urlParam ="";
        if (params!=null && params.size()>0){
            Iterator<String> iter = params.keySet().iterator();
            while (iter.hasNext()){
                String key = iter.next();
                urlParam = urlParam + "&"+key +"="+params.get(key);
            }
        }

        String[] temp =url.split("\\?");
        if(temp.length==1){
            if(urlParam.length()>0){
                url = url + "?"+urlParam.substring(1);
            }
        }else{
                url = url + urlParam;
        }
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<T> rss = restTemplate.exchange(url, method, requestEntity, responseType);
        return rss;
    }

    private <T> ResponseEntity<T> request(String url, HttpMethod method, Map<String, ?> params,Class<T> responseType) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        Object access = request.getSession().getAttribute("x-access-token");
        if (access==null ){
            logger.warn("无法加入access-token头部，用户请求路径："+request.getRequestURI()+",API请求路径："+url);
            access="";
        }
        Map<String,String> headers=new HashMap<>();
        headers.put("x-access-token",String.valueOf(access));

        return request(url,method,headers,params,responseType);
    }
}
