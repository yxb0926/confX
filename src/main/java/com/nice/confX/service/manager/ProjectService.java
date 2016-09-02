package com.nice.confX.service.manager;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by yxb on 16/7/1.
 */
@Service
public interface ProjectService {

    /**
    *  增加项目情况
    * */
    Integer addProject(String pcode, String pname, String powner, String pdesc, String ptype, String pfilename);

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
     *  查询某个工程的项目信息
     * */
    List queryProject(String pname);

    /**
     *  检查groupname是否已经存在
     * */
    Boolean checkExist(String dataid,String groupid);

    /**
     *  添加工程
     * */
    Integer addProgram(HttpServletRequest request);

    /**
     *  查询全部工程
     * */

    List queryAllProgram();


}

