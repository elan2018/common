package com.elan.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * ID生成器
 */
public class GenerationUtil {


    public static String createTokenKey(String prefix,String str){
        Assert.notNull(prefix,"前缀名称不允许为空！");
        Assert.notNull(str,"关键内容不允许为空！");
        if (StringUtils.isNotEmpty(prefix) && StringUtils.isNotEmpty(str)) {
            return  clearInvalidChar(prefix + String.valueOf(str.hashCode()));
        }
        return null;
    }


    public static String clearInvalidChar(String str){
        if(StringUtils.isNotEmpty(str)){
            str = str.replaceAll("\\W+","_");
            str = str.substring(0,1).replaceAll("\\d+","_")
                  +  str.substring(1);
        }
        return str;
    }

    public static String uuid(){
        return UUID.randomUUID().toString();
    }
    public static void main(String[] args){
        System.out.println(GenerationUtil.clearInvalidChar("3abd-efw=3232,.sfde"));

    }
}
