package com.nice.confX.service.manager.impl;

import com.alibaba.fastjson.JSON;
import com.nice.confX.model.ClientEvent;
import com.nice.confX.model.ClientProperties;
import com.nice.confX.service.manager.ClientService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    @Transactional
    public void clientReplace(String appname, String iplist) throws Exception {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_created = simpleDateFormat.format(date);
        String gmt_modified = gmt_created;

        String[] ips = iplist.trim().split(",");

        try {
            String sql_del = "DELETE FROM client_list WHERE pname=?";
            jdbcTemplate.update(sql_del,appname);
            logger.info("del old info from table client_list.");
        }catch (DataAccessException e){
            logger.error(e);
        }

        for (int i=0; i< ips.length; i++){
            try {
                String sql = "REPLACE INTO " +
                        "client_list (pname, client_ip, gmt_created, gmt_modified) " +
                        "VALUES(?,?,?,?)";
                jdbcTemplate.update(sql, appname, ips[i].trim(), gmt_created, gmt_modified);
                logger.info("client ip 添加成功!");
            }catch (DataAccessException e){
                logger.error(e);
            }
        }
    }

    @Override
    public List getClientInfo(String appname) {
        List myList = new ArrayList();
        try{
            String sql = "SELECT *" +
                    " FROM client_list" +
                    " WHERE pname=? AND isdel=?";
            myList = jdbcTemplate.queryForList(sql,appname,0);

        }catch (DataAccessException e){
            logger.error(e);
        }
        return myList;

    }

    @Override
    public String getClientIps(String appname) {
        String ipstr = "";
        try {
            List myList = new ArrayList();
            String sql = "SELECT client_ip" +
                    " FROM client_list" +
                    " WHERE pname=? AND isdel=?";
            myList = jdbcTemplate.queryForList(sql, appname, 0);
            for (int i=0; i< myList.size(); i++){
                Map map = (Map) myList.get(i);
                ipstr += map.get("client_ip");
                ipstr += ",";
                ipstr += "\n";
            }
        }catch (DataAccessException e){
            logger.error(e);
        }
        return ipstr;
    }

    @Transactional
    @Override
    public Map clientHeartBeat(HttpServletRequest request) {
        String ip   = request.getParameter("ip");
        String port = request.getParameter("port");
        String hostname = request.getParameter("hostname");

        String event  = request.getParameter("event");
        String properties = request.getParameter("properties");

        ClientEvent clientEvent = JSON.parseObject(event, ClientEvent.class);
        ClientProperties clientProperties =
                JSON.parseObject(properties, ClientProperties.class);

        Map map = new HashMap();
        map.put("status", 202); // 200 表示ok, 201表示更新失败, 202表示查询失败, 203表示ip未注册;
        map.put("msg", "");
        map.put("heartbeat_interval", 10); // 检测周期,单位为秒;
        map.put("items","");

        try {
            java.util.Date date = new java.util.Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String gmt_modified = simpleDateFormat.format(date);

            String sql = "UPDATE client_list " +
                    "SET gmt_modified=?, client_port=?, hostname=?, uptime=?, client_launch_time=?," +
                    "event_msg=?, event_code=?, data=? " +
                    "WHERE client_ip=?";
            int rows = jdbcTemplate.update(sql,
                    gmt_modified,
                    port, hostname,
                    clientProperties.getUptime(),
                    clientProperties.getLaunch_time(),
                    clientEvent.getEvent_defined().get("msg"),
                    clientEvent.getEvent_defined().get("code"),
                    clientEvent.getData(),
                    ip);

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
            String sql = "SELECT p.pcode AS item,p.ptype AS type, p.pfilename AS filename, " +
                    "pp.path AS path, c.pname AS pname, c.isdel AS isdel, " +
                    "pp.pcmd AS cmd, pp.psysuser AS sysuser, pp.pcodetype AS codetype " +
                    "FROM project_info p,client_list c, project_project pp " +
                    "WHERE c.pname=p.pname AND c.client_ip=? " +
                    "AND pp.pname=c.pname  AND pp.pstatus=?";
            /**
            String sql = "SELECT pcode AS item, ptype AS type " +
                    " FROM client_list " +
                    " WHERE client_ip = ? " +
                    " GROUP BY pcode,ptype";
            **/
            myList = jdbcTemplate.queryForList(sql, ip, "online");

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

    @Override
    @Transactional
    public void clientDel(String program, String ip) throws Exception {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_modified = simpleDateFormat.format(date);

        String sql =
                "UPDATE client_list " +
                "SET isdel=?,gmt_modified=? " + "WHERE pname=? AND client_ip=?";
        jdbcTemplate.update(sql, 1,gmt_modified, program, ip);
    }
}
