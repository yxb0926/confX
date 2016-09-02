package com.nice.confX.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/7.
 */
public class JsonUtil {
    public HashMap jsonToMap(String jsonStr) {

        ContentModel contentModel = JSON.parseObject(jsonStr, ContentModel.class);
        String groupid = contentModel.getGroupid();
        String dbname = contentModel.getDbname();
        String dataid = contentModel.getDataid();
        JSONObject dbkey = contentModel.getDbkey();
        com.alibaba.fastjson.JSONArray masterArr
                = (com.alibaba.fastjson.JSONArray) dbkey.get("master");
        com.alibaba.fastjson.JSONArray slaveArr
                = (com.alibaba.fastjson.JSONArray) dbkey.get("slave");
        Attach attach = JSON.parseObject(dbkey.get("attach").toString(), Attach.class);


        HashMap clusterMap = new HashMap();
        List masterList    = new ArrayList();
        List slaveList     = new ArrayList();
        Map attachMap      = new HashMap();

        /** Master */
        masterList = genClusterList(masterArr);

        /** Slave */
        slaveList = genClusterList(slaveArr);

        /** Attach */
        attachMap.put("user",     attach.getUser());
        attachMap.put("passwd",   attach.getPasswd());
        attachMap.put("tbprefix", attach.getTbprefix());
        attachMap.put("charset",  attach.getCharset());
        attachMap.put("timeout",  attach.getTimeout());

        clusterMap.put("master", masterList);
        clusterMap.put("slave", slaveList);
        clusterMap.put("attach", attachMap);

        return clusterMap;
    }

    public List genClusterList(com.alibaba.fastjson.JSONArray jsonArray) {
        List list = new ArrayList();

        for (int i = 0; i < jsonArray.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            ClusterModel clusterModel = jsonArray.getObject(i, ClusterModel.class);
            map.put("ip", clusterModel.getIp());
            map.put("port", clusterModel.getPort());

            list.add(map);
        }
        return list;
    }

    public Map myContentToMap(String jsonStr){
        ContentModel contentModel = JSON.parseObject(jsonStr, ContentModel.class);
        String groupid = contentModel.getGroupid();
        String dbname = contentModel.getDbname();
        String dataid = contentModel.getDataid();
        JSONObject dbkey = contentModel.getDbkey();
        com.alibaba.fastjson.JSONArray masterArr =
                (com.alibaba.fastjson.JSONArray) dbkey.get("master");
        com.alibaba.fastjson.JSONArray slaveArr  =
                (com.alibaba.fastjson.JSONArray) dbkey.get("slave");
        Attach attach = JSON.parseObject(dbkey.get("attach").toString(), Attach.class);

        HashMap clusterMap = new HashMap();
        Map attachMap      = new HashMap();

        /** Master */
        List masterList = genClusterList(masterArr);

        /** Slave */
        List slaveList = genClusterList(slaveArr);

        /** Attach */
        attachMap.put("user",     attach.getUser());
        attachMap.put("passwd",   attach.getPasswd());
        attachMap.put("tbprefix", attach.getTbprefix());
        attachMap.put("charset",  attach.getCharset());
        attachMap.put("timeout",  attach.getTimeout());

        Map dbkeyMap = new HashMap();
        dbkeyMap.put("slave", slaveList);
        dbkeyMap.put("attach", attachMap);
        dbkeyMap.put("master", masterList);


        clusterMap.put("dbkey",   dbkeyMap);
        clusterMap.put("dbname",  dbname);
        clusterMap.put("groupid", groupid);
        clusterMap.put("dataid",  dataid);

        return clusterMap;
    }

    public Map redisConentToMap(String jsonStr){
        RedisContentModel redisContentModel = JSON.parseObject(jsonStr, RedisContentModel.class);
        String dataid  = redisContentModel.getDataid();
        String groupid = redisContentModel.getGroupid();
        JSONObject dbkey   = redisContentModel.getDbkey();

        com.alibaba.fastjson.JSONArray masterArr =
                (com.alibaba.fastjson.JSONArray) dbkey.get("master");

        com.alibaba.fastjson.JSONArray slaveArr  =
                (com.alibaba.fastjson.JSONArray) dbkey.get("slave");

        RedisAttach redisAttach =
                JSON.parseObject(dbkey.get("attach").toString(), RedisAttach.class);

        Map attachMap      = new HashMap();
        attachMap.put("read_timeout", redisAttach.getRead_timeout());
        attachMap.put("timeout", redisAttach.getTimeout());

        /** Master */
        List masterList = genClusterList(masterArr);
        List slaveList  = genClusterList(slaveArr);

        Map dbkeyMap = new HashMap();
        dbkeyMap.put("master", masterList);
        dbkeyMap.put("slave",  slaveList);
        dbkeyMap.put("attach", attachMap);

        HashMap clusterMap = new HashMap();

        clusterMap.put("dbkey",   dbkeyMap);
        clusterMap.put("groupid", groupid);
        clusterMap.put("dataid",  dataid);

        return clusterMap;
    }
}
