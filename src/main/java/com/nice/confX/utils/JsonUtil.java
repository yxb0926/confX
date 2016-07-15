package com.nice.confX.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nice.confX.model.Attach;
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
    public HashMap jsonToMap(String jsonStr) {

        ContentModel contentModel = JSON.parseObject(jsonStr, ContentModel.class);
        String groupid = contentModel.getGroupid();
        String dbname = contentModel.getDbname();
        String dataid = contentModel.getDataid();
        JSONObject dbkey = contentModel.getDbkey();
        com.alibaba.fastjson.JSONArray masterArr = (com.alibaba.fastjson.JSONArray) dbkey.get("master");
        com.alibaba.fastjson.JSONArray slaveArr = (com.alibaba.fastjson.JSONArray) dbkey.get("slave");
        Attach attach = JSON.parseObject(dbkey.get("attach").toString(), Attach.class);


        HashMap clusterMap = new HashMap();
        List masterList = new ArrayList();
        List slaveList = new ArrayList();
        Map attachMap = new HashMap();

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

    public Map contentToMap(String jsonStr){

        ContentModel contentModel = JSON.parseObject(jsonStr, ContentModel.class);
        String groupid = contentModel.getGroupid();
        String dbname = contentModel.getDbname();
        String dataid = contentModel.getDataid();
        JSONObject dbkey = contentModel.getDbkey();
        com.alibaba.fastjson.JSONArray masterArr =
                (com.alibaba.fastjson.JSONArray) dbkey.get("master");
        com.alibaba.fastjson.JSONArray slaveArr =
                (com.alibaba.fastjson.JSONArray) dbkey.get("slave");
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
}
