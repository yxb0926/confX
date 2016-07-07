package com.nice.confX.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yxb on 16/6/30.
 */

@Controller
@RequestMapping("/conf")
public class ConfController {

    @RequestMapping("/index")
    public String index(){
        return "conf/index";
    }
}
