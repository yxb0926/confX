package com.nice.confX.controller;

import com.nice.confX.service.manager.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
    @Autowired
    private ProjectService projectService;

    @RequestMapping("/project/index")
    public ModelAndView index(String pname) {
        ModelAndView modelAndView = new ModelAndView("manager/project/index");
        List list = new ArrayList();
        if (pname != null && pname !=""){
            list = projectService.queryProject(pname);
        } else {
            list = projectService.queryAllProject();
        }
        modelAndView.addObject("plist", list);
        modelAndView.addObject("pname", pname);
        return modelAndView;

    }

    @RequestMapping("/project/new")
    public ModelAndView newproject(String pname) {
        ModelAndView modelAndView = new ModelAndView("manager/project/new");
        modelAndView.addObject("pname", pname);

        return modelAndView;
    }

    @RequestMapping("/project/add")
    public ModelAndView addproject(String pcode,  String pname,
                                   String powner, String pdesc,
                                   String ptype,  String pfilename) {
        Integer success = projectService.addProject(pcode, pname, powner, pdesc, ptype, pfilename);
        if (success == 1) {
            return new ModelAndView("redirect:index?pname="+pname);
        } else {
            return new ModelAndView("redirect:new?pname="+pname);
        }
    }

    @RequestMapping(value = "/project/del", method = RequestMethod.POST)
    @ResponseBody
    public Map delProject(String programName, String projectName, String type){
        Map map = new HashMap();
        map.put("status", 200);
        map.put("msg", "Del Sucess!");

        try {
            projectService.delProject(programName, projectName, type);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status", 201);
            map.put("msg", "Del Failed!");
        }
        return map;
    }
}
