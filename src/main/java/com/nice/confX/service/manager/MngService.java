package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by yxb on 16/7/5.
 */

@Service
public interface MngService {
    /**
     * 添加一个mysql的配置
     *
     *
     * @param request*/
    Map addConf(HttpServletRequest request);

    /**
     * 删除一个mysql的配置
     * */
    Integer delConf();

    /**
     * 修改一个mysql的配置
     * */
    Integer modifyConf();

    /**
     * 检测一个mysql的配置
     * */
    Integer checkConf();

    Map getConf(String dataid);
    Map getConf(String dataid, String groupid);

}
