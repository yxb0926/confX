package com.nice.confX.model;

/**
 * Created by yxb on 16/7/11.
 */
public class Attach {
    String user;
    String passwd;
    String tbprefix;
    String charset;
    String timeout;

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

    public String getTbprefix() {
        return tbprefix;
    }

    public void setTbprefix(String tbprefix) {
        this.tbprefix = tbprefix;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
