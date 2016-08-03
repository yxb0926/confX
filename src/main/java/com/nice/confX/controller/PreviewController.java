package com.nice.confX.controller;

import com.nice.confX.service.manager.impl.MySQLServiceImpl;
import com.nice.confX.service.preview.PreConfService;
import com.nice.confX.service.preview.impl.PreConfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
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
    public Object mysqlIndex(String items){

        Map map = preConfService.getConf(items);
        return map;
    }
}
