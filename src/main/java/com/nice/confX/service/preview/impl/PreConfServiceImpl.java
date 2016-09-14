package com.nice.confX.service.preview.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.service.manager.DataSourceFactory;
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

    private Logger logger = Logger.getLogger(PreConfServiceImpl.class);

    @Autowired
    DataSourceFactory dataSourceFactory;

    @Override
    public Map getConf(String type, String item, String pname) {
        logger.info("Get Conf -- " + "Type:" + type + "ProgramName:" + pname + " Appname:" + item);

        MngService service = dataSourceFactory.getService(type);
        return service.getConf(item,pname);
    }

}
