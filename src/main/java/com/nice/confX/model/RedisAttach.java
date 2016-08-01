package com.nice.confX.model;

/**
 * Created by yxb on 16/8/1.
 */
public class RedisAttach {
    int read_timeout;
    int timeout;

    public int getRead_timeout() {
        return read_timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setRead_timeout(int read_timeout) {
        this.read_timeout = read_timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
