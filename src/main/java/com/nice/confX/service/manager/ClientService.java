package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/25.
 */
@Service
public interface ClientService {
    public Object clientReplace(String appname, String iplist);

    public List getClientInfo(String appname);

    public String getClientIps(String appname);

    public Map clientHeartBeat(String ip);

    public void clientDel(String groupname, String ip) throws Exception;

}
