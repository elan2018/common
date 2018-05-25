package com.elan.common.response;

import java.io.Serializable;

public class ResponseResult implements Serializable{
    private Integer code=-1;
    private String message="";
    private Object data;

    public ResponseResult(){

    }
    public ResponseResult(Object data){
        this.code = 0;
        this.message = "ok";
        this.data = data;
    }
    public ResponseResult(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    public ResponseResult(Integer code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
