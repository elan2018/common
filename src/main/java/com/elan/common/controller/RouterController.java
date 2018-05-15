package com.elan.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RouterController {

    @Value("${page.router.dir:/demo}")
    private String dir;
    /**
     * 通用路由（不支持参数）
     * @param page
     * @return
     */
    @RequestMapping(value = "${page.router.path:/html}/{page}",method = RequestMethod.GET)
    public String router(@PathVariable  String page){
        String[] fg = page.split("-");
        String url="";
        for(String p :fg){
            url  +="/"+p;
        }
        return dir+url;
    }
}
