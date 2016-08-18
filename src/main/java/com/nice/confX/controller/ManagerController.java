package com.nice.confX.controller;

import com.nice.confX.service.manager.ClientService;
import com.nice.confX.service.manager.DataSourceFactory;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.service.manager.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    DataSourceFactory dataSourceFactory;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ClientService clientService;

    @Autowired
    @Qualifier("mysql")
    private MngService mysqlService;


    @RequestMapping("/project/program")
    public ModelAndView project(){
        ModelAndView modelAndView = new ModelAndView("manager/project/program");
        List pList = projectService.queryAllProgram();
        modelAndView.addObject("pList", pList);

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
    public ModelAndView addproject(String pcode, String pname, String powner, String pdesc, String ptype) {
        Integer success = projectService.addProject(pcode, pname, powner, pdesc, ptype);
        if (success == 1) {
            return new ModelAndView("redirect:index?pname="+pname);
        } else {
            return new ModelAndView("redirect:new?pname="+pname);
        }
    }

    @RequestMapping(value = "/project/confadd", method = RequestMethod.POST)
    public String addconf(HttpServletRequest httpServletRequest,
                          RedirectAttributes redirectAttributes) {
        String ptype = httpServletRequest.getParameter("ptype");
        String pcode = httpServletRequest.getParameter("pappname");
        MngService service = dataSourceFactory.getService(ptype);
        Map urlMap = dataSourceFactory.getUrl(ptype, pcode);

        try {
            service.addConf(httpServletRequest);
            return "redirect:"+urlMap.get("okurl");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            redirectAttributes.addAttribute("errmsg", "添加失败,请检查后重新添加!");

            return "redirect:"+urlMap.get("errurl");
        }
    }

    @RequestMapping(value = "/project/confdel", method = RequestMethod.POST)
    @ResponseBody
    public Map delconf(HttpServletRequest httpServletRequest){
        Map map = new HashMap();
        map.put("code", 200);
        map.put("msg", "ok");
        String type        = httpServletRequest.getParameter("type");
        String appname     = httpServletRequest.getParameter("appname");
        String groupname   = httpServletRequest.getParameter("groupname");
        MngService service = dataSourceFactory.getService(type);

        try {
            service.delConf(appname, groupname, type);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code",201);
            map.put("msg", "failed");
        }
        return map;
    }


    /**
    * 修改接口
    * */
    @RequestMapping(value = "/project/confmodify", method = RequestMethod.POST)
    public String modifyconf(HttpServletRequest httpServletRequest,
                             RedirectAttributes redirectAttributes){
        String type        = httpServletRequest.getParameter("ptype");
        String appname     = httpServletRequest.getParameter("pappname");
        MngService service = dataSourceFactory.getService(type);
        Map         urlMap = dataSourceFactory.getUrl(type, appname);

        try {
            service.modifyConf(httpServletRequest);
            return "redirect:"+urlMap.get("okurl");
        }catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addAttribute("errmsg", "修改失败,请检查后重新修改!");

            return "redirect:"+urlMap.get("errurl");
        }
    }

    /**
     *  check接口
     *
     * */

    @RequestMapping(value = "/project/confcheck", method = RequestMethod.POST)
    @ResponseBody
    public Map checkconf(HttpServletRequest httpServletRequest,
                            RedirectAttributes redirectAttributes){

        String type =  httpServletRequest.getParameter("type");
        String appname = httpServletRequest.getParameter("appname");
        String groupname = httpServletRequest.getParameter("groupname");

        MngService service = dataSourceFactory.getService(type);
        Map map = new HashMap();
        try {
            map = service.checkConf(appname, groupname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping("/project/ngxconf")
    public ModelAndView ngxconf() {
        return new ModelAndView("manager/nginx/ngxconf");
    }

    @RequestMapping("/project/errconf")
    public ModelAndView errconf() {
        return new ModelAndView("manager/error/errconf");
    }

    @RequestMapping("/project/pconf")
    public ModelAndView pconf(String pcode, String ptype) {
        if (ptype.equals("MySQL")) {
            return new ModelAndView("redirect:myconf?pcode=" + pcode);
        } else if (ptype.equals("Redis")) {
            return new ModelAndView("redirect:rdsconf?pcode=" + pcode);
        } else if (ptype.equals("Nginx")) {
            return new ModelAndView("redirect:ngxconf");
        } else {
            return new ModelAndView("redirect:errconf");
        }
    }

    @RequestMapping("/project/clientadd")
    public ModelAndView cltConf(String pname) {
        ModelAndView modelAndView = new ModelAndView("manager/project/clientadd");
        String ipstr = clientService.getClientIps(pname);

        modelAndView.addObject("ipstr", ipstr);
        modelAndView.addObject("pcode", pname);

        return modelAndView;
    }

    @RequestMapping("/project/clientreplace")
    @ResponseBody
    public Object clientReplace(String pappname, String pclientlist) {
        if (pclientlist.length() > 0 && pclientlist != "") {
            Object res = clientService.clientReplace(pappname, pclientlist);
            return res;
        } else {
            logger.error("Client List Is Null, Add Client Info Failed!");
            return null;
        }

    }

    @RequestMapping(value = "/project/clientconf", method = RequestMethod.GET)
    public Object clientInfo(String pname) {
        ModelAndView modelAndView = new ModelAndView("manager/project/clientconf");

        List myList = new ArrayList();
        myList = clientService.getClientInfo(pname);
        modelAndView.addObject("clientinfo", myList);
        modelAndView.addObject("pname", pname);
        return modelAndView;
    }
}

