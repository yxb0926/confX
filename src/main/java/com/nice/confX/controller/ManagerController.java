package com.nice.confX.controller;

import com.nice.confX.service.manager.ClientService;
import com.nice.confX.service.manager.SwitchService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.service.manager.ProjectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    private ClientService clientService;

    @Autowired
    private SwitchService switchService;

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

    @RequestMapping(value = "/project/confadd", method = RequestMethod.POST)
    @ResponseBody
    public Map addconf(HttpServletRequest httpServletRequest){
        MngService service = switchService.getService(httpServletRequest.getParameter("ptype"));
        Map resMap = service.addConf(httpServletRequest);
        return resMap;
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
            return new ModelAndView("redirect:rdsconf?pcode="+pcode);
        } else if (ptype.equals("Nginx")) {
            return new ModelAndView("redirect:ngxconf");
        } else {
            return new ModelAndView("redirect:errconf");
        }
    }

    @RequestMapping("/project/clientadd")
    public ModelAndView cltConf(String ptype, String pcode){
        ModelAndView modelAndView = new ModelAndView("manager/project/clientadd");
        String ipstr = clientService.getClientIps(pcode, ptype);

        modelAndView.addObject("ipstr", ipstr);
        modelAndView.addObject("ptype", ptype);
        modelAndView.addObject("pcode", pcode);

        return modelAndView;
    }

    @RequestMapping("/project/clientreplace")
    @ResponseBody
    public Object clientReplace(String pappname, String ptype, String pclientlist){
        if ( pclientlist.length() >0 && pclientlist != ""){
            Object res = clientService.clientReplace(pappname, ptype, pclientlist);
            return res;
        }else{
            logger.error("Client List Is Null, Add Client Info Failed!");
            return null;
        }

    }

    @RequestMapping(value = "/project/clientconf", method = RequestMethod.GET)
    public Object clientInfo(String pcode, String ptype){
        ModelAndView modelAndView = new ModelAndView("manager/project/clientconf");

        List myList = new ArrayList();
        myList = clientService.getClientInfo(pcode, ptype);
        modelAndView.addObject("clientinfo", myList);
        modelAndView.addObject("pcode",      pcode);
        modelAndView.addObject("ptype",      ptype);
        return modelAndView;
    }
}

