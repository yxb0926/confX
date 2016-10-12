package com.nice.confX.controller;

import com.nice.confX.model.User;
import com.nice.confX.service.manager.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/9/6.
 */
@Controller
@RequestMapping("/manager")
public class ProjectController {

    private Logger logger = Logger.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @RequestMapping("/project/index")
    public ModelAndView index(HttpServletRequest request, String pname) {
        User sessionUser = (User) request.getSession().getAttribute("sessionUser");
        ModelAndView modelAndView = new ModelAndView("manager/project/index");
        List list = new ArrayList();


        list = projectService.getProject(request);

        /**
        if (pname != null && pname !=""){
            list = projectService.queryProject(pname);
        } else {
            list = projectService.queryAllProject();
        }
        */
        modelAndView.addObject("plist", list);
        modelAndView.addObject("pname", pname);
        modelAndView.addObject("role", sessionUser.getRole());

        return modelAndView;
    }

    @RequestMapping("/project/new")
    public ModelAndView newproject(HttpServletRequest request, String pname) {
        User sessionUser = (User) request.getSession().getAttribute("sessionUser");

        ModelAndView modelAndView = new ModelAndView("manager/project/new");
        modelAndView.addObject("pname", pname);
        modelAndView.addObject("role", sessionUser.getRole());

        return modelAndView;
    }

    @RequestMapping(value = "/project/add", method = RequestMethod.POST)
    public ModelAndView addproject(HttpServletRequest request,
                                   String pcode,  String pname,
                                   String pdesc,
                                   String ptype,  String pfilename) {

        User sessionUser = (User) request.getSession().getAttribute("sessionUser");
        Integer success = 0 ;
        if (sessionUser !=null && sessionUser.getRole().equals("admin")){
            success = projectService.addProject(pcode, pname, pdesc, ptype, pfilename);
        }
        if (success == 1) {
            return new ModelAndView("redirect:index?pname="+pname);
        } else {
            return new ModelAndView("redirect:new?pname="+pname);
        }
    }

    @RequestMapping(value = "/project/del", method = RequestMethod.POST)
    @ResponseBody
    public Map delProject(HttpServletRequest request, String programName, String projectName, String type){

        User sessionUser = (User) request.getSession().getAttribute("sessionUser");

        Map map = new HashMap();
        map.put("status", 200);
        map.put("msg", "Del Sucess!");

        try {
            // 登陆,且权限为admin的才可以删除
            if ( sessionUser != null  && sessionUser.getRole().equals("admin") ){
                projectService.delProject(programName, projectName, type);
            }else {
                map.put("status", 202) ;
                map.put("msg", "该用户无删除权限!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", 201);
            map.put("msg", "Del Failed!");
        }
        return map;
    }
}
