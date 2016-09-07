package com.nice.confX.model;

/**
 * Created by yxb on 16/8/1.
 */
public class RedisAttach {
    int read_timeout;
    String timeout;

    public int getRead_timeout() {
        return read_timeout;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setRead_timeout(int read_timeout) {
        this.read_timeout = read_timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
