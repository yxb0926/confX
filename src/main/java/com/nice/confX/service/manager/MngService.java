package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/5.
 */

@Service
public interface MngService {
    /**
     * 添加一个mysql|redis的配置
     * @param request*/
    void addConf(HttpServletRequest request) throws Exception;

    /**
     * 删除一个mysql|redis的配置
     * */
    void delConf(String appname, String pname, String groupname, String type) throws Exception;

    /**
     * 修改一个mysql|redis的配置
     * */
    void modifyConf(HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 检测一个mysql|redis的配置
     * */
    Map checkConf(String appname, String pname, String groupname, String type) throws Exception;
    Map checkConf(String appname, String pname, String type) throws Exception;

    Map getConf(String dataid, String pname, String type);
    Map getConf(String dataid, String pname, String groupid, String type);

}
