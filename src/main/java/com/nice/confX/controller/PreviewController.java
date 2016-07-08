package com.nice.confX.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by yxb on 16/7/7.
 */

@Controller
@RequestMapping("/preview")
public class PreviewController {

    @RequestMapping("/index")
    public ModelAndView index(){

        ModelAndView modelAndView = new ModelAndView("preview/index");

        return modelAndView;
    }

    @RequestMapping("/MySQL/index")
    @ResponseBody
    public String mysqlIndex(String pcode, String groupid){

        if ( pcode == null || pcode.length() <=0 ){
            // 如果不指定pcode,则直接返回null
            return null;
        }else if (groupid == null || groupid.length() <=0 ){
            // 获取整个pcode的数据
        }else {
            // 获取pcode下的groupid的数据,
            // 注意,groupid可以是一串id|id|id,可以获取单个或多个groupid

        }


        return "xxxxxxxxxxxxxx";
    }
}
