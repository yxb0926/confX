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
    public String mysqlIndex(){

        return null;
    }
}
