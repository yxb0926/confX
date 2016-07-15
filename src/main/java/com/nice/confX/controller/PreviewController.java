package com.nice.confX.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.service.manager.MySQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * Created by yxb on 16/7/7.
 */

@Controller
@RequestMapping("/preview")
public class PreviewController {

    @Autowired
    private MySQLService mySQLService;


    @RequestMapping("/index")
    public ModelAndView index(){

        ModelAndView modelAndView = new ModelAndView("preview/index");

        return modelAndView;
    }

    @RequestMapping("/MySQL/index")
    @ResponseBody
    public Object mysqlIndex(String pcode, String groupid){

        Map mapOut = new HashMap();
        if ( pcode == null || pcode.length() <=0 ){
            // 如果不指定pcode,则直接返回null
            return null;
        }else if (groupid == null || groupid.length() <=0 ){
            // 获取整个pcode的数据
            mapOut = mySQLService.getMyConf(pcode);
        }else {
            // 获取pcode下的groupid的数据,
            // 注意,groupid可以是一串id|id|id,可以获取单个或多个groupid
            mapOut = mySQLService.getMyConf(pcode, groupid);
        }

        return mapOut;
    }
}
