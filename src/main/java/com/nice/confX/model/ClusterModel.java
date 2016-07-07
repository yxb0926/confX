package com.nice.confX.model;

import com.alibaba.fastjson.JSONArray;

/**
 * Created by yxb on 16/7/6.
 */
public class ClusterModel {
    String ip;
    String port;
    String user;
    String passwd;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
