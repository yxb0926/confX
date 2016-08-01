package com.nice.confX.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/8/1.
 */
public class ListToMap {

    public Map mylistToMap(List list){
        Map outMap = new HashMap();
        JsonUtil jsonUtil = new JsonUtil();
        if (list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                Map tmpMap = new HashMap();
                Map myMap = (Map) list.get(i);

                String groupname = myMap.get("group_id").toString();
                String myConent = myMap.get("content").toString();
                tmpMap.put("content", jsonUtil.myContentToMap(myConent));
                tmpMap.put("md5",     myMap.get("md5").toString());

                outMap.put(groupname, tmpMap);
            }
        }

        return outMap;
    }

    public Map redisListToMap(List list){
        Map outMap = new HashMap();
        JsonUtil jsonUtil = new JsonUtil();

        if(list.size()>0){
            for (int i=0; i<list.size(); i++){
                Map tmpMap = new HashMap();
                Map redisMap = (Map) list.get(i);

                String groupname = redisMap.get("group_id").toString();
                String redisContent = redisMap.get("content").toString();
                tmpMap.put("content", jsonUtil.redisConentToMap(redisContent));
                tmpMap.put("md5",     redisMap.get("md5").toString());

                outMap.put(groupname, tmpMap);
            }

        }
        return outMap;
    }
}
