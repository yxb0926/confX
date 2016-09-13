package com.nice.confX.controller;

import com.nice.confX.model.User;
import com.nice.confX.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by yxb on 16/7/15.
 */

@Controller
@RequestMapping("/")
public class BaseController {

    private Logger logger = Logger.getLogger(BaseController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView indexroot(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:manager/login");
        //User sessionUser = (User) request.getSession().getAttribute("sessionUser");
        return modelAndView;
    }

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("redirect:manager/login");
        return modelAndView;
    }

}
