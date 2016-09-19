package com.nice.confX.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.model.ItemModel;
import com.nice.confX.service.manager.impl.MySQLServiceImpl;
import com.nice.confX.service.preview.PreConfService;
import com.nice.confX.service.preview.impl.PreConfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


/**
 * Created by yxb on 16/7/7.
 */

@Controller
@RequestMapping("/preview")
public class PreviewController {

    @Autowired
    private PreConfService preConfService;


    @RequestMapping("/index")
    public ModelAndView index(){

        ModelAndView modelAndView = new ModelAndView("preview/index");

        return modelAndView;
    }


    @RequestMapping(value = "/conf/pull", method = RequestMethod.POST)
    @ResponseBody
    public Map mysqlIndex(@RequestBody ItemModel requestBody, HttpServletRequest request) {
        Map map = preConfService.getConf(
                requestBody.getType(),
                requestBody.getItem(),
                requestBody.getPname(),
                request.getRemoteAddr()
        );
        return map;
    }
}
