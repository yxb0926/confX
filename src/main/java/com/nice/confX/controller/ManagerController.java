package com.nice.confX.controller;

import com.nice.confX.service.manager.MySQLService;
import com.nice.confX.service.manager.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/6/28.
 */

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private Logger logger = Logger.getLogger(ManagerController.class);

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

        Map myMap = new HashMap();
        if ( pcode == null || pcode.length() <=0 ){
            return null;
        }else if( groupid == null || groupid.length() <=0 ){
            myMap = mySQLService.getMyConf(pcode);
        }else {
            myMap = mySQLService.getMyConf(pcode, groupid);
        }

        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconf");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pmymap", myMap);

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
    public ModelAndView myconfnew(String pcode, String errmsg){
        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconfnew");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("errmsg", errmsg);

        return modelAndView;
    }

    @RequestMapping("/project/myconfadd")
    public ModelAndView myconfadd(HttpServletRequest request){

        String dataid  = request.getParameter("pcode");
        String groupid = request.getParameter("pgroupname");

        /** 检查该配置是否已经存在,如果存在则不能添加项目,并返回错误提示*/
        if (groupid == "" || groupid.length() <= 0) {
            String errmsg = "GroupName Is Null!";
            ModelAndView modelAndView = new ModelAndView("redirect:myconfnew?pcode=" +
                    dataid + "&errmsg=" + errmsg);
            return  modelAndView;
        }else if ( projectService.checkExist(dataid, groupid) ){
            String errmsg = "GroupName Already Exists!";
            ModelAndView modelAndView = new ModelAndView("redirect:myconfnew?pcode=" +
                    dataid + "&errmsg=" + errmsg);
            modelAndView.addObject("errmsg", "GroupName:" + dataid + "已经存在,不得重复添加!");
            return  modelAndView;
        }

        Integer res = mySQLService.addConf(request);
        if (res == 1){
            return new ModelAndView("redirect:myconf?pcode="+dataid);
        }else {
            String errmsg = "Some Err, Please Try Again!";
            ModelAndView modelAndView = new ModelAndView("redirect:myconfnew?pcode=" +
                    dataid + "&errmsg=" + errmsg);
            return  modelAndView;
        }

    }
}
