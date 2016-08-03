package com.nice.confX.service.preview;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by yxb on 16/7/28.
 */

@Service
public interface PreConfService {
    public Map getConf(String type, String item);
}
