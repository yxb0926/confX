package com.nice.confX.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yxb on 16/8/1.
 */
public class RedisContentModel {
    String dataid;
    String groupid;
    JSONObject dbkey;

    public String getDataid() {
        return dataid;
    }

    public String getGroupid() {
        return groupid;
    }

    public JSONObject getDbkey() {
        return dbkey;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setDbkey(JSONObject dbkey) {
        this.dbkey = dbkey;
    }
}
