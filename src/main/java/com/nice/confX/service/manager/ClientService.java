package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/25.
 */
@Service
public interface ClientService {
    public Object clientReplace(String appname, String type, String iplist);

    public List getClientInfo(String appname, String type);

    public String getClientIps(String appname, String type);

    public Map ClientHeartBeat(String ip);


}
