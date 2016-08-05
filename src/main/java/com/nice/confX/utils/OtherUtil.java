package com.nice.confX.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/8/5.
 */
public class OtherUtil {
    public String listToString(List list){
        String ipstr = "";
        for (int i=0; i<list.size(); i++){
            Map map = (Map) list.get(i);
            ipstr += map.get("ip");
            ipstr += ":";
            ipstr += map.get("port");
            ipstr += ",";
            ipstr += "\n";
        }

        return ipstr;
    }
}
