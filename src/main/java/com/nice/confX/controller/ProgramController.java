package com.nice.confX.controller;

import com.nice.confX.model.User;
import com.nice.confX.service.manager.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
public class ProgramController extends HandlerInterceptorAdapter {
    @Autowired
    private ProjectService projectService;


    @RequestMapping("/project/program")
    public ModelAndView project( HttpServletRequest request ){
        User sessionUser = (User) request.getSession().getAttribute("sessionUser");

        ModelAndView modelAndView = new ModelAndView("manager/project/program");
        List pList = new ArrayList();
        String iport;
        if (request.getParameter("iport") == null){
            pList = projectService.queryAllProgram();
        }else {
            iport = request.getParameter("iport").trim();
            pList = projectService.queryProgramUseIport(iport);
        }

        modelAndView.addObject("pList", pList);
        modelAndView.addObject("role", sessionUser.getRole());

        return modelAndView;
    }

    @RequestMapping("/project/newprogram")
    public ModelAndView newprogram() {
        return new ModelAndView("manager/project/newprogram");
    }

    @RequestMapping("/project/addprogram")
    public ModelAndView addprogram(HttpServletRequest request) {
        Integer success = projectService.addProgram(request);
        if (success == 1){
            return new ModelAndView("redirect:program");
        } else {

            return new ModelAndView("redirect:newprogram");
        }
    }

    @RequestMapping(value = "/project/delprogram", method = RequestMethod.POST)
    @ResponseBody
    public Map delProgram(String pname){
        Map map = new HashMap();
        map.put("status", 200);
        map.put("msg", "Del Success!");
        try {
            projectService.delProgram(pname);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", 201);
            map.put("msg", "Del Failed!");
        }
        return map;
    }


    @RequestMapping("/project/modifyprogram")
    public ModelAndView modifyProgram(String pname){
        ModelAndView modelAndView = new ModelAndView("manager/project/programmodify");
        List list = projectService.queryProgram(pname);

        modelAndView.addObject("plist", list);

        return modelAndView;
    }
}
