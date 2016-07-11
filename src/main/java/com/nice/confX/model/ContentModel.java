package com.nice.confX.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yxb on 16/7/6.
 */
public class ContentModel {
    String dbname;
    String groupid;
    String dataid;
    JSONObject dbkey;

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public JSONObject getDbkey() {
        return dbkey;
    }

    public void setDbkey(JSONObject dbkey) {
        this.dbkey = dbkey;
    }
}
