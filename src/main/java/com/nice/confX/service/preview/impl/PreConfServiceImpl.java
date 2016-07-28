package com.nice.confX.service.preview.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.service.preview.PreConfService;
import com.nice.confX.utils.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/28.
 */

@Service
public class PreConfServiceImpl implements PreConfService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = Logger.getLogger(PreConfServiceImpl.class);

    @Override
    public Map getConf(String items) {
        JSONObject jsonObject = JSON.parseObject(items);
        String type = jsonObject.getString("type");
        String item = jsonObject.getString("item");
        logger.info("Type:" + type + " Appname:" + item);

        if (type.equalsIgnoreCase("MySQL")){
            return getMysqlConf(item);
        }else if (type.equalsIgnoreCase("Redis")){
            return getRedisConf(item);
        }else if (type.equalsIgnoreCase("Nginx")){
            return getNginxConf(item);
        }else {
            logger.error("暂时不支持该类型:" + type);
        }

        return null;
    }

    @Override
    public Map getMysqlConf(String dataid) {
        Map map = new HashMap();

        List myList = null;
        List outList = new ArrayList();
        Map outMap = new HashMap();
        try {
            myList = jdbcTemplate.queryForList("SELECT data_id, group_id, dbname, content, md5 " +
                    "FROM config_info WHERE data_id=?", dataid);
        }catch (DataAccessException e){
            logger.error(e);
            map.put("status", 201);
            map.put("msg",    "查询失败");
            map.put("item_content","");
            return map;
        }

        JsonUtil jsonUtil = new JsonUtil();
        if (myList.size()>0) {
            for (int i = 0; i < myList.size(); i++) {
                Map tmpMap = new HashMap();
                Map myMap = (Map) myList.get(i);

                String groupname = myMap.get("group_id").toString();
                String myConent = myMap.get("content").toString();
                tmpMap.put("content", jsonUtil.contentToMap(myConent));
                tmpMap.put("md5",     myMap.get("md5").toString());

                outMap.put(groupname, tmpMap);
            }
            map.put("status",        200);
            map.put("msg",           "ok");
            map.put("item_content",  outMap);

            return map;
        }else {
            map.put("status",       202);
            map.put("msg",          "未查到该记录");
            map.put("item_content", "");

            return map;
        }
    }

    @Override
    public Map getRedisConf(String item) {
        return null;
    }

    @Override
    public Map getNginxConf(String item) {
        return null;
    }
}
