package com.nice.confX.service.preview;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by yxb on 16/7/28.
 */

@Service
public interface PreConfService {
    public Map getConf(String items);

    public Map getMysqlConf(String dataid);

    public Map getRedisConf(String item);

    public Map getNginxConf(String item);
}