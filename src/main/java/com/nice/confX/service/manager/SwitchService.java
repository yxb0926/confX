package com.nice.confX.service.manager;

import com.nice.confX.service.manager.impl.MySQLServiceImpl;
import com.nice.confX.service.manager.impl.ProjectServiceImpl;
import com.nice.confX.service.manager.impl.RedisServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yxb on 16/7/29.
 */
@Service
public class SwitchService {

    private Logger logger = Logger.getLogger(SwitchService.class);

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    private MySQLServiceImpl mySQLService;

    @Autowired
    private RedisServiceImpl redisService;

    public MngService getService(String type){
        MngService service = null;
        if( type.equals("MySQL")){
            service = mySQLService;
        }else if(type.equals("Redis")){
            service = redisService;
        }

        return service;
    }
}
