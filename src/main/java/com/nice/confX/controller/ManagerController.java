package com.nice.confX.controller;

import com.nice.confX.service.manager.MySQLService;
import com.nice.confX.service.manager.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxb on 16/6/28.
 */

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private MySQLService mySQLService;

    @RequestMapping("/project/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView("manager/project/index");
        List allProject = projectService.queryAllProject();
        modelAndView.addObject("plist", allProject);
        return modelAndView;
    }

    /**
    @RequestMapping("/addproject")
    @ResponseBody
    public String addproject(String project){
        System.out.println(project);
        return "Success:1";
    }
    */

    @RequestMapping("/project/new")
    public ModelAndView newproject(){
       return new ModelAndView("manager/project/new");
    }

    @RequestMapping("/project/add")
    public ModelAndView addproject(String pcode, String pname, String powner, String pdesc, String ptype){
        Integer success = projectService.addProject(pcode,pname,powner,pdesc,ptype);
        if (success == 1){
            return new ModelAndView("redirect:index");
        }else {
            return new ModelAndView("redirect:new");
        }
    }

    @RequestMapping("/project/myconf")
    public ModelAndView myconf(String pcode, String groupid){
        /**
         *  pcode为项目编码不能为空,如果为空则返回错误页面;
         *  groupid为空,表示查找该pcode下的所有groupid的信息;
         *  groupid可以为单个或多个id|id|id ...
         * */

        List myList = new ArrayList();
        if ( pcode == null || pcode.length() <=0 ){
            return null;
        }else if( groupid == null || groupid.length() <=0 ){
            myList = mySQLService.getAllMyConf(pcode);
        }else {
            myList = mySQLService.getOneMyConf(pcode, groupid);
        }


        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconf");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pmylist", myList);


        return modelAndView;
    }


    @RequestMapping("/project/rdsconf")
    public ModelAndView rdsconf(){
        return new ModelAndView("manager/redis/rdsconf");
    }

    @RequestMapping("/project/ngxconf")
    public ModelAndView ngxconf(){
        return new ModelAndView("manager/nginx/ngxconf");
    }

    @RequestMapping("/project/errconf")
    public ModelAndView errconf(){
        return new ModelAndView("manager/error/errconf");
    }

    @RequestMapping("/project/pconf")
    public ModelAndView pconf(String pcode, String ptype){
        if (ptype.equals("MySQL")) {
            return new ModelAndView("redirect:myconf?pcode="+pcode);
        } else if (ptype.equals("Redis")) {
            return new ModelAndView("redirect:rdsconf");
        } else if (ptype.equals("Nginx")) {
            return new ModelAndView("redirect:ngxconf");
        } else {
            return new ModelAndView("redirect:errconf");
        }
    }

    @RequestMapping("/project/myconfnew")
    public ModelAndView myconfnew(String pcode){
        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconfnew");
        modelAndView.addObject("pcode", pcode);

        return modelAndView;
    }

    @RequestMapping("/project/myconfadd")
    public ModelAndView myconfadd(HttpServletRequest request){
        Integer res = mySQLService.addConf(request);

        return new ModelAndView("redirect:myconf?pcode="+request.getParameter("pcode"));
    }
}
