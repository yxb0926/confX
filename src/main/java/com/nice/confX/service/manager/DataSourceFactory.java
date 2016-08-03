package com.nice.confX.service.manager;

import com.nice.confX.service.manager.impl.MySQLServiceImpl;
import com.nice.confX.service.manager.impl.ProjectServiceImpl;
import com.nice.confX.service.manager.impl.RedisServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yxb on 16/7/29.
 */
@Service
public class DataSourceFactory {

    private Logger logger = Logger.getLogger(DataSourceFactory.class);

    @Autowired
    @Qualifier("mysql")
    private MngService mySQLService;

    @Autowired
    @Qualifier("redis")
    private MngService redisService;

    public MngService getService(String type) {
        MngService service = null;
        if (type.equals("MySQL")) {
            service = mySQLService;
        } else if (type.equals("Redis")) {
            service = redisService;
        }
        return service;
    }

    public Map getUrl(String type, String pcode){
        Map map = new HashMap() ;
        String okURL  = "";
        String errURL = "";

        if (type.equals("MySQL")){
            map.put("okurl",  "/manager/project/myconf?pcode="+pcode);
            map.put("errurl", "/manager/project/myconfnew?pcode="+pcode);
        }else if (type.equals("Redis")){
            map.put("okurl",  "/manager/project/rdsconf?pcode="+pcode);
            map.put("errurl", "/manager/project/rdsconfnew?pcode="+pcode);
        }

        return map;
    }
}
