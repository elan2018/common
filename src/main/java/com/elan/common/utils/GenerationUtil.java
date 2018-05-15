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
            return  "csrf_token_" + String.valueOf(str.hashCode());
        }

        return null;

    }

    public static String uuid(){
        return UUID.randomUUID().toString();
    }
}
