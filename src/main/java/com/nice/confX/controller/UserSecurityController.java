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

/**
 * Created by yxb on 16/9/13.
 */
@Controller
@RequestMapping("/manager")
public class UserSecurityController {

    private Logger logger = Logger.getLogger(UserSecurityController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/project/login", method = RequestMethod.GET)
    public ModelAndView loginindex(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        User sessionUser = (User) request.getSession().getAttribute("sessionUser");
        if ( sessionUser != null){
            modelAndView.setViewName("redirect:program");
        } else {
            modelAndView.setViewName("manager/project/login");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/project/login", method = RequestMethod.POST)
    public ModelAndView login (HttpServletRequest request, String username, String password){
        ModelAndView modelAndView = new ModelAndView();

        User sessionUser = (User) request.getSession().getAttribute("sessionUser");

        Object result = userService.login(username, password);
        if (result instanceof User) {
            logger.info(username + " Login Success!");
            request.getSession().setMaxInactiveInterval(60 * 60 * 24);  // 过期时间为1天
            request.getSession().setAttribute("sessionUser", result);

            modelAndView.setViewName("redirect:program");

        } else {
            //登陆失败
            logger.info(username + " " + result);
            modelAndView.setViewName("manager/project/login");
            modelAndView.addObject("msg", result);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/project/logout", method = RequestMethod.GET)
    public ModelAndView logout(){
        ModelAndView modelAndView = new ModelAndView("manager/project/logout");

        return modelAndView;
    }

}
