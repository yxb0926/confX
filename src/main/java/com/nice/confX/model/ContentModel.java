package com.nice.confX.model;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Array;

/**
 * Created by yxb on 16/7/6.
 */
public class ContentModel {
    String dbname;
    String tbprefix;
    String charset;
    String timeout;
    JSONArray master;
    JSONArray slave;

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setTbprefix(String tbprefix) {
        this.tbprefix = tbprefix;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public void setMaster(JSONArray master) {
        this.master = master;
    }

    public void setSlave(JSONArray slave) {
        this.slave = slave;
    }

    public String getDbname() {
        return dbname;
    }

    public String getTbprefix() {
        return tbprefix;
    }

    public String getCharset() {
        return charset;
    }

    public String getTimeout() {
        return timeout;
    }

    public JSONArray getMaster() {
        return master;
    }

    public JSONArray getSlave() {
        return slave;
    }
}
