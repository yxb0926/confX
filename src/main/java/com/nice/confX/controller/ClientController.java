package com.nice.confX.controller;

import com.nice.confX.service.manager.ClientService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by yxb on 16/7/27.
 */
@Controller
@RequestMapping("/client")
public class ClientController {
    private Logger logger = Logger.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    @ResponseBody
    public Map heartBeat(HttpServletRequest request, String ip){
        logger.info(ip + " - "+ request.getParameter("event"));
        return clientService.clientHeartBeat(request);
    }
}
