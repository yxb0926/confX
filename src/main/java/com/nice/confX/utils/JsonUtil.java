package com.nice.confX.utils;

import com.alibaba.fastjson.JSON;
import com.nice.confX.model.ClusterModel;
import com.nice.confX.model.ContentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/7.
 */
public class JsonUtil {
    public HashMap jsonToMap(String jsonStr){

        ContentModel contentModel = JSON.parseObject(jsonStr, ContentModel.class);
        com.alibaba.fastjson.JSONArray masterArr = contentModel.getMaster();
        com.alibaba.fastjson.JSONArray slaveArr = contentModel.getSlave();

        HashMap clusterMap = new HashMap();
        List masterList  = new ArrayList();
        List slaveList   = new ArrayList();

        /**
         * master 信息
         * */
        for (int i=0; i<masterArr.size(); i++){
            HashMap<String, String> map = new HashMap<String, String>();
            ClusterModel clusterInfo = masterArr.getObject(i, ClusterModel.class);
            map.put("ip",     clusterInfo.getIp());
            map.put("port",   clusterInfo.getPort());
            map.put("user",   clusterInfo.getUser());
            map.put("passwd", clusterInfo.getPasswd());

            masterList.add(map);
        }

        /**
         * slave 信息
         * */
        for (int j=0; j<slaveArr.size(); j++){
            HashMap<String, String> map = new HashMap<String, String>();
            ClusterModel clusterInfo = slaveArr.getObject(j, ClusterModel.class);
            map.put("ip",     clusterInfo.getIp());
            map.put("port",   clusterInfo.getPort());
            map.put("user",   clusterInfo.getUser());
            map.put("passwd", clusterInfo.getPasswd());

            slaveList.add(map);
        }


        clusterMap.put("master", masterList);
        clusterMap.put("slave",  slaveList);
        clusterMap.put("dbname", contentModel.getDbname());
        clusterMap.put("tbprefix", contentModel.getTbprefix());
        clusterMap.put("charset", contentModel.getCharset());
        clusterMap.put("timeout", contentModel.getTimeout());

        return clusterMap;
    }
}
