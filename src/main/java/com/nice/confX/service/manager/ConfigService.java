package com.nice.confX.service.manager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List getGroupCnt(){
        List list1 = new ArrayList();
        try {
            String sql = "select count(group_id) as cnt,type from config_info group by type";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

            list1 = new ArrayList();
            for(int i=0;i<list.size();i++){
                List tmplist = new ArrayList();
                tmplist.add(list.get(i).get("type"));
                tmplist.add(list.get(i).get("cnt"));
                list1.add(tmplist);
            }

        }catch (SQLWarningException e){
            e.printStackTrace();
        }
        return list1;
    }

    public List getHostCnt(){
        List list = new ArrayList();
        try{
            String sql_mysql = "select count(distinct ip) AS cnt from groupname_info_mysql";
            String sql_redis = "select count(distinct ip) AS cnt from groupname_info_redis";

            List<Map<String, Object>> list_mysql = jdbcTemplate.queryForList(sql_mysql);
            List<Map<String, Object>> list_redis = jdbcTemplate.queryForList(sql_redis);

            List tmplistmysql = new ArrayList();
            List tmplistredis = new ArrayList();

            tmplistmysql.add("mysql");
            tmplistmysql.add(list_mysql.get(0).get("cnt"));

            tmplistredis.add("redis");
            tmplistredis.add(list_redis.get(0).get("cnt"));

            list.add(tmplistmysql);
            list.add(tmplistredis);

        }catch (SQLWarningException e){
            e.printStackTrace();
        }
        return list;
    }
}
