package com.nice.confX.service.preview.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.service.manager.SwitchService;
import com.nice.confX.service.preview.PreConfService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Created by yxb on 16/7/28.
 */

@Service
public class PreConfServiceImpl implements PreConfService {

    @Autowired
    private SwitchService switchService;

    private Logger logger = Logger.getLogger(PreConfServiceImpl.class);

    @Override
    public Map getConf(String items) {
        JSONObject jsonObject = JSON.parseObject(items);
        String type = jsonObject.getString("type");
        String item = jsonObject.getString("item");
        logger.info("Type:" + type + " Appname:" + item);

        MngService service = switchService.getService(type);
        return service.getConf(item);
    }

}
