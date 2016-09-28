package com.nice.confX.service.manager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yxb on 16/7/29.
 */
@Service
public class ConfigService {

    private Logger logger = Logger.getLogger(ConfigService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List getConf(String dataid, String pname, String type){
        String sql = "";
        sql = "SELECT program_id, data_id, group_id, content, md5 " +
                "FROM config_info WHERE data_id=? AND program_id=? AND type=?";

        return getData(sql, dataid, pname, type);
    }


    public List getConf(String dataid, String pname, String groupid, String type){
        String sql = "";
        String[] groupidx = groupid.split("\\|");
        String group_id = "";

        for (int i = 0; i <groupidx.length; i++){
            group_id = group_id + "'" + groupidx[i] + "'";
            if (i < groupidx.length-1){
                group_id += ",";
            }
        }

        sql = "SELECT data_id, group_id, content, md5 " +
                "FROM config_info " +
                "WHERE data_id=? AND program_id=? AND group_id in (" + group_id + ") AND type=?";

        return getData(sql, dataid, pname, type);
    }


    protected List getData(String sql, String dataid, String pname, String type){
        try {
            return jdbcTemplate.queryForList(sql, dataid, pname, type);
        }catch (DataAccessException e){
            logger.error(e);
            return new ArrayList();
        }
    }


}
