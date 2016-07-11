package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/1.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
    *  增加项目情况
     *  返回值: 1 => success, 0 => failed
    * */
    @Override
    public Integer addProject(String pcode, String pname, String powner, String pdesc, String ptype){
        try {
            jdbcTemplate.update("INSERT INTO project_info(pcode, pname, powner, pdesc, ptype) " +
                    "VALUE (?,?,?,?,?)", pcode, pname, powner, pdesc, ptype);
            return 1;
        }catch (DataAccessException dataAccessException){
            return 0;
        }
    }


    /**
     *  删除项目
     * */
    @Override
    public Integer delProject(){
        return 0;
    }

    /**
     *  查询项目信息
     * */
    @Override
    public List<String> queryProject(){
        List<String>  pList = new ArrayList<String>();
        return pList;
    }

    /**
     *  查询全部项目信息
     * */
    @Override
    public List queryAllProject(){
        List pAllList = new ArrayList();
        try{
            pAllList = jdbcTemplate.queryForList("SELECT * FROM project_info");
        }catch (DataAccessException dae){
            System.out.println(dae);
            return pAllList;
        }
        return pAllList;
    }

    @Override
    public Boolean checkExist(String dataid, String groupid) {
        try{
            String sql = "SELECT COUNT(1) AS cnt FROM config_info WHERE data_id=? AND group_id=? ";
            List<Map<String, Object>> rs = jdbcTemplate.queryForList(sql, dataid, groupid);

            Number number = (Number) rs.get(0).get("cnt");
            int cnt = number.intValue();

            if ( cnt > 0 ){
                return true;
            }else {
                return false;
            }

        }catch (DataAccessException e){
            System.out.println(e);
            return true;
        }
    }
}
