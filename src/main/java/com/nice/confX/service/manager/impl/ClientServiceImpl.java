package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.ClientService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/25.
 */
@Service
public class ClientServiceImpl implements ClientService{
    private Logger logger = Logger.getLogger(ClientServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object clientReplace(String appname, String type, String iplist) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_created = simpleDateFormat.format(date);
        String gmt_modified = gmt_created;

        String[] ips = iplist.trim().split(",");

        try {
            String sql_del = "DELETE FROM client_list WHERE pcode=? AND ptype=?";
            jdbcTemplate.update(sql_del,appname, type);
            logger.info("del old info from table client_list.");
        }catch (DataAccessException e){
            logger.error(e);
        }

        for (int i=0; i< ips.length; i++){
            try {
                String sql = "REPLACE INTO " +
                        "client_list (pcode, ptype, client_ip, gmt_created, gmt_modified) " +
                        "VALUES(?,?,?,?,?)";
                jdbcTemplate.update(sql, appname, type, ips[i].trim(), gmt_created, gmt_modified);
                logger.info("client ip 添加成功!");
            }catch (DataAccessException e){
                logger.error(e);
            }
        }

        return null;
    }

    @Override
    public List getClientInfo(String appname, String type) {
        List myList = new ArrayList();
        try{
            String sql = "SELECT pcode, ptype, client_ip, gmt_created, gmt_modified" +
                    " FROM client_list" +
                    " WHERE pcode=? AND ptype=?";
            myList = jdbcTemplate.queryForList(sql,appname,type);

        }catch (DataAccessException e){
            logger.error(e);
        }
        return myList;

    }

    @Override
    public String getClientIps(String appname, String type) {
        String ipstr = "";
        try {
            List myList = new ArrayList();
            String sql = "SELECT client_ip" +
                    " FROM client_list" +
                    " WHERE pcode=? AND ptype=?";
            myList = jdbcTemplate.queryForList(sql, appname, type);
            for (int i=0; i< myList.size(); i++){
                Map map = (Map) myList.get(i);
                ipstr += map.get("client_ip");
                ipstr += ",";
                ipstr += "\n";
            }
        }catch (DataAccessException e){
            logger.error(e);
        }
        logger.info(ipstr);

        return ipstr;
    }

    @Override
    public Map ClientHeartBeat(String ip) {
        Map map = new HashMap();

        map.put("status", 202); // 200 表示ok, 201表示更新失败, 202表示查询失败, 203表示ip未注册;
        map.put("msg", "");
        map.put("heartbeat_interval", 10); // 检测周期,单位为秒;
        map.put("items","");

        try {
            java.util.Date date = new java.util.Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String gmt_modified = simpleDateFormat.format(date);

            String sql = "UPDATE client_list SET gmt_modified=? WHERE client_ip=?";
            int rows = jdbcTemplate.update(sql,gmt_modified,ip);
            if(rows == 0){
                map.put("status",203);
                map.put("msg", "client ip:"+ip+"未注册");
                return map;
            }


        }catch (DataAccessException e){
            map.put("status", 201);
            logger.error(e);

            return map;
        }

        try {
            List myList = new ArrayList();
            String sql = "SELECT pcode AS item, ptype AS type " +
                    " FROM client_list " +
                    " WHERE client_ip = ? " +
                    " GROUP BY pcode,ptype;";
            myList = jdbcTemplate.queryForList(sql, ip);

            map.put("status", 200);
            map.put("msg", "ok");
            map.put("heartbeat_interval", 10);
            map.put("items", myList);

            return map;

        }catch (DataAccessException e){
            map.put("status", 202);
            map.put("msg", "client ip:"+ip+"查询失败.");
            logger.error(e);

            return map;
        }
    }
}
