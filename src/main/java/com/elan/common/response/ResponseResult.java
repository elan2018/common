package com.elan.common.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseResult implements Serializable{
    private Integer code=-1;
    private String message="";
    private Object data;
    private List info;

    public ResponseResult(){
        this.code = -1;
        this.message = "";
        this.data = null;
        this.info =null;
    }
    public ResponseResult(Object data){
        this.code = 0;
        this.message = "ok";
        this.data = data;
        this.info =null;
    }
    public ResponseResult(Object data,List info){
        this.code = 0;
        this.message = "ok";
        this.data = data;
        this.info =info;
    }
    public ResponseResult(Integer code,String message){
        this.code = code;
        this.message = message;
        this.data = null;
        this.info = null;

    }
    public ResponseResult(Integer code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
        this.info = null;
    }
    public ResponseResult(Integer code,String message,Object data,List info){
        this.code = code;
        this.message = message;
        this.data = data;
        this.info = info;
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

    public List getInfo() {
        return info;
    }

    public void setInfo(List info) {
        this.info = info;
    }
}
