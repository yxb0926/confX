package com.nice.confX.controller;

import com.nice.confX.model.User;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.OtherUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/29.
 */
@Controller
@RequestMapping("/manager")
public class RedisController {

    @Autowired
    @Qualifier("redis")
    private MngService redisService;

    @RequestMapping("/project/rdsconf")
    public ModelAndView rdsconf(HttpServletRequest request, String pcode, String pname, String groupid ){
        Map map = new HashMap();
        User sessionUser = (User) request.getSession().getAttribute("sessionUser");

        if ( pcode == null || pcode.length() <=0 ){
            return null;
        }else if( groupid == null || groupid.length() <=0 ){
            map = redisService.getConf(pcode,pname);
        }else {
            map = redisService.getConf(pcode, pname, groupid);
        }

        ModelAndView modelAndView = new ModelAndView("manager/redis/rdsconf");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pname", pname);
        modelAndView.addObject("pmap", map);
        modelAndView.addObject("role", sessionUser.getRole());

        return modelAndView;
    }

    @RequestMapping("/project/rdsconfnew")
    public ModelAndView rdsconfnew(String pcode, String pname, String errmsg){
        ModelAndView modelAndView = new ModelAndView("manager/redis/rdsconfnew");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pname", pname);
        modelAndView.addObject("errmsg", errmsg);

        return modelAndView;
    }

    @RequestMapping("/project/rdsconfmodify")
    public ModelAndView rdsconfmodify(String pcode, String pname, String pgroupname){
        ModelAndView modelAndView = new ModelAndView("manager/redis/rdsconfmodify");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pname", pname);

        Map map = redisService.getConf(pcode, pname, pgroupname);

        modelAndView.addObject("pcontent", map.get("item_content"));

        Map dbkeymap = (Map) ((Map) ((Map) ((Map)map.get("item_content")).
                get(pgroupname)).
                get("content")).
                get("dbkey");

        List masterList = (List) dbkeymap.get("master");
        List slaveList  = (List) dbkeymap.get("slave");
        OtherUtil otherUtil = new OtherUtil();
        String masterStr = otherUtil.listToString(masterList);
        String slaveStr  = otherUtil.listToString(slaveList);
        modelAndView.addObject("pmasterstr",masterStr);
        modelAndView.addObject("pslavestr", slaveStr);

        return modelAndView;
    }
}
