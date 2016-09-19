package com.nice.confX.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yxb on 16/9/19.
 */
public class ClientEvent {
    String data;
    JSONObject event_defined;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public JSONObject getEvent_defined() {
        return event_defined;
    }

    public void setEvent_defined(JSONObject event_defined) {
        this.event_defined = event_defined;
    }
}
