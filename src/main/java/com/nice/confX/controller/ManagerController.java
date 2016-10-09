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
    private ClientService clientService;

    @RequestMapping("/project/ngxconf")
    public ModelAndView ngxconf() {
        return new ModelAndView("manager/nginx/ngxconf");
    }

    @RequestMapping("/project/errconf")
    public ModelAndView errconf() {
        return new ModelAndView("manager/error/errconf");
    }

    @RequestMapping("/project/pconf")
    public ModelAndView pconf(String pcode, String ptype, String pname) {
        if (ptype.equals("MySQL")) {
            return new ModelAndView("redirect:myconf?pcode="  + pcode + "&pname=" + pname);
        } else if (ptype.equals("Redis")) {
            return new ModelAndView("redirect:rdsconf?pcode=" + pcode + "&pname=" + pname);
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

    @RequestMapping(value = "/project/clientdel", method = RequestMethod.POST)
    @ResponseBody
    public Map clientDel(String groupname, String ip){
        Map map = new HashMap();
        map.put("code", 200);
        map.put("msg",  "ok");
        try {
            clientService.clientDel(groupname, ip);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 201);
            map.put("msg",  "failed");
        }
        return map;
    }

    @RequestMapping("/project/clientreplace")
    public ModelAndView clientReplace(String pappname, String pclientlist) {
        if (pclientlist.length() > 0 && pclientlist != "") {
            try {
                ModelAndView modelAndView = new ModelAndView("redirect:clientconf?pname="+pappname);
                clientService.clientReplace(pappname, pclientlist);
                return modelAndView;
            } catch (Exception e) {
                ModelAndView modelAndView = new ModelAndView("redirect:clientadd?pname="+pappname);
                modelAndView.addObject("errmsg","添加失败!");
                e.printStackTrace();
                return modelAndView;
            }
        } else {
            ModelAndView modelAndView = new ModelAndView("redirect:clientadd?pname="+pappname);
            modelAndView.addObject("errmsg", "添加失败,Client List 不能为空!");
            logger.error("Client List Is Null, Add Client Info Failed!");
            return modelAndView;
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


    @RequestMapping(value = "/project/copyconf", method = RequestMethod.GET)
    public ModelAndView copyconf(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("manager/project/copyconf");

        return modelAndView;
    }
}

