package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/1.
 */
@Service
public interface ProjectService {

    /**
    *  增加项目情况
    * */
    Integer addProject(String pcode, String pname, String powner, String pdesc, String ptype);

    /**
     *  删除项目
     * */
    Integer delProject();

    /**
     *  查询项目信息
     * */
    List<String> queryProject();

    /**
     *  查询全部项目信息
     * */
    List queryAllProject();

    /**
     *  检查groupname是否已经存在
     * */
    Boolean checkExist(String dataid,String groupid);

}

