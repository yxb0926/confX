package com.nice.confX.controller;

import com.nice.confX.service.manager.MngService;
import com.nice.confX.service.manager.impl.MySQLServiceImpl;
import com.nice.confX.service.manager.impl.ProjectServiceImpl;
import com.nice.confX.utils.JsonUtil;
import com.nice.confX.utils.OtherUtil;
import org.apache.log4j.Logger;
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
public class MysqlController {
    private Logger logger = Logger.getLogger(MysqlController.class);

    @Autowired
    @Qualifier("mysql")
    private MngService mySQLService;

    @RequestMapping("/project/myconfnew")
    public ModelAndView myconfnew(String pcode, String errmsg) {
        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconfnew");
        modelAndView.addObject("pcode",     pcode);
        modelAndView.addObject("errmsg",    errmsg);
        return modelAndView;
    }

    @RequestMapping("/project/myconfmodify")
    public ModelAndView myconfmodify(String pcode, String pgroupname){
        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconfmodify");
        Map map = mySQLService.getConf(pcode, pgroupname);
        modelAndView.addObject("pcode",     pcode);
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

    @RequestMapping("/project/myconf")
    public ModelAndView myconf(String pcode, String groupid) {
        /**
         *  pcode为项目编码不能为空,如果为空则返回错误页面;
         *  groupid为空,表示查找该pcode下的所有groupid的信息;
         *  groupid可以为单个或多个id|id|id ...
         * */

        Map myMap = new HashMap();
        if (pcode == null || pcode.length() <= 0) {
            return null;
        } else if (groupid == null || groupid.length() <= 0) {
            myMap = mySQLService.getConf(pcode);
        } else {
            myMap = mySQLService.getConf(pcode, groupid);
        }

        ModelAndView modelAndView = new ModelAndView("manager/mysql/myconf");
        modelAndView.addObject("pcode", pcode);
        modelAndView.addObject("pmymap", myMap);

        return modelAndView;
    }
}
