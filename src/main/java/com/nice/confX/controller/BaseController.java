package com.nice.confX.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by yxb on 16/7/15.
 */

@Controller
@RequestMapping("/")
public class BaseController {


    @RequestMapping("/")
    public ModelAndView indexroot(){
        ModelAndView modelAndView = new ModelAndView("redirect:manager/project/program");
        return modelAndView;
    }

    @RequestMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("redirect:manager/project/index");
        return modelAndView;
    }
}
