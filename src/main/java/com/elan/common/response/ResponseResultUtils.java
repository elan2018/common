package com.elan.common.response;

public class ResponseResultUtils {
    public static ResponseResult success(Object data){
        return new ResponseResult(0,"ok",data);
    }

    public static ResponseResult success(String message,Object data){
        return new ResponseResult(0,message,data);
    }
    public static ResponseResult error(Integer code,String message){
        return new ResponseResult(code,message,null);
    }
    public static ResponseResult error(Integer code,String message,Object data){
        return new ResponseResult(code,message,data);
    }

}
