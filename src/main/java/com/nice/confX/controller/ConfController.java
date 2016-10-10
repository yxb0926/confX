package com.nice.confX.controller;

import com.nice.confX.service.manager.DataSourceFactory;
import com.nice.confX.service.manager.MngService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yxb on 16/6/30.
 */

@Controller
@RequestMapping("/manager")
public class ConfController {

    private Logger logger = Logger.getLogger(ConfController.class);
    @Autowired
    DataSourceFactory dataSourceFactory;

    /**
     *  check接口
     *
     * */
    @RequestMapping(value = "/project/confcheck", method = RequestMethod.POST)
    @ResponseBody
    public Map checkconf(HttpServletRequest httpServletRequest,
                         RedirectAttributes redirectAttributes){

        String type      = httpServletRequest.getParameter("type");
        String appname   = httpServletRequest.getParameter("appname");
        String pname     = httpServletRequest.getParameter("pname") ;
        String groupname = httpServletRequest.getParameter("groupname");

        MngService service = dataSourceFactory.getService(type);
        Map map = new HashMap();
        try {
            if (groupname.equals("") || groupname == null){
                map = service.checkConf(appname,pname,type);
            } else {
                map = service.checkConf(appname, pname, groupname, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        String pname       = httpServletRequest.getParameter("pname");
        MngService service = dataSourceFactory.getService(type);
        Map         urlMap = dataSourceFactory.getUrl(type, appname, pname);

        try {
            service.modifyConf(httpServletRequest);
            return "redirect:"+urlMap.get("okurl");
        }catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addAttribute("errmsg", "修改失败,请检查后重新修改!");

            return "redirect:"+urlMap.get("errurl");
        }
    }

    @RequestMapping(value = "/project/confadd", method = RequestMethod.POST)
    public String addconf(HttpServletRequest httpServletRequest,
                          RedirectAttributes redirectAttributes) {
        String ptype = httpServletRequest.getParameter("ptype");
        String pcode = httpServletRequest.getParameter("pappname");
        String pname = httpServletRequest.getParameter("pname");
        MngService service = dataSourceFactory.getService(ptype);
        Map urlMap = dataSourceFactory.getUrl(ptype, pcode, pname);

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
        String pname       = httpServletRequest.getParameter("pname");
        String groupname   = httpServletRequest.getParameter("groupname");
        MngService service = dataSourceFactory.getService(type);

        try {
            service.delConf(appname, pname, groupname, type);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code",201);
            map.put("msg", "failed");
        }
        return map;
    }

    @RequestMapping(value = "/project/copyconf", method = RequestMethod.GET)
    public ModelAndView copyconf(){
        ModelAndView modelAndView = new ModelAndView("manager/project/copyconf");

        return modelAndView;
    }

    @RequestMapping(value = "/project/copyconf", method = RequestMethod.POST)
    public ModelAndView copyconf(HttpServletRequest request){
        Map map = new HashMap();

        map.put("code", 200);
        map.put("data","");
        map.put("msg", "OK!");


        String type = request.getParameter("ftype");
        MngService service = dataSourceFactory.getService(type);
        ModelAndView modelAndView = new ModelAndView("manager/project/resconf");
        try {
            service.copyConf(request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            map.put("msg", e);
        }

        modelAndView.addObject("data", map);
        return modelAndView;
    }
}
