package com.nice.confX.controller;

import com.nice.confX.service.manager.impl.ProjectServiceImpl;
import com.nice.confX.service.manager.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yxb on 16/7/29.
 */
@Controller
@RequestMapping("/manager")
public class RedisController {

    @Autowired
    private RedisServiceImpl redisService;

    @RequestMapping("/project/rdsconf")
    public ModelAndView rdsconf(String pcode, String groupid ){
        Map map = new HashMap();

        if ( pcode == null || pcode.length() <=0 ){
            return null;
        }else if( groupid == null || groupid.length() <=0 ){
            map = redisService.getConf(pcode);
        }else {
            map = redisService.getConf(pcode, groupid);
        }

        ModelAndView modelAndView = new ModelAndView("manager/redis/rdsconf");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pmap", map);

        return modelAndView;
    }

    @RequestMapping("/project/rdsconfnew")
    public ModelAndView rdsconfnew(String pcode, String errmsg){
        ModelAndView modelAndView = new ModelAndView("manager/redis/rdsconfnew");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("errmsg", errmsg);

        return modelAndView;
    }
}
