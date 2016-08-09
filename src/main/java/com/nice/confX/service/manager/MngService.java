package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    void addConf(HttpServletRequest request) throws Exception;

    /**
     * 删除一个mysql|redis的配置
     * */
    void delConf(String appname, String groupname, String type) throws Exception;

    /**
     * 修改一个mysql|redis的配置
     * */
    void modifyConf(HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 检测一个mysql|redis的配置
     * */
    Map checkConf(String appname, String groupname) throws Exception;

    Map getConf(String dataid);
    Map getConf(String dataid, String groupid);

}
