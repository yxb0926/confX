package com.nice.confX.controller;

import com.nice.confX.service.manager.ClientService;
import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.DataSourceFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    private ConfigService configService;

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

    @RequestMapping("/project/dashboard")
    public Object clientDashBoard(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("manager/project/dashboard");

        List errList = clientService.getErrClient();
        List allList = clientService.getAllClient();
        List<Map<String, Object>> top10progarm = clientService.getTop10Program();
        List ccollect = clientService.getCollectClient();
        List grouplist = configService.getGroupCnt();
        List hostlist  = configService.getHostCnt();

        Map map = new HashMap();
        List top10list = new ArrayList();
        for (int i=0; i<top10progarm.size(); i++){

            List tmplist = new ArrayList();
            tmplist.add(top10progarm.get(i).get("pname"));
            tmplist.add(top10progarm.get(i).get("cnt"));

            top10list.add(tmplist);

        }

        modelAndView.addObject("errlist", errList);
        modelAndView.addObject("alllist", allList);
        modelAndView.addObject("top10p",  top10list);
        modelAndView.addObject("collect", ccollect);
        modelAndView.addObject("group",   grouplist);
        modelAndView.addObject("hostx",   hostlist);

        return modelAndView;
    }
}

