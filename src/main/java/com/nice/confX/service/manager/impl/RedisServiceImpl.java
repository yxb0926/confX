package com.nice.confX.service.manager.impl;

import com.alibaba.fastjson.JSON;
import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.ListToMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/28.
 */
@Service
public class RedisServiceImpl implements MngService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigService configService;

    ListToMap listToMap = new ListToMap();

    private Logger logger = Logger.getLogger(RedisServiceImpl.class);

    @Override
    public Map addConf(HttpServletRequest request) {
        Map msg = new HashMap();
        msg.put("status", 201);
        msg.put("msg",    "添加失败");

        String type        = request.getParameter("ptype");
        String dataid      = request.getParameter("pappname");
        String groupid     = request.getParameter("pgroupname");
        String timeout     = request.getParameter("ptimeout");
        String readtimeout = request.getParameter("preadtimeout");
        String ipport      = request.getParameter("pmiport");


        String[] iport = ipport.trim().split(":");
        String ip   = iport[0];
        String port = iport[1];

        Map map = new HashMap();
        map.put("dataid",  dataid);
        map.put("groupid", groupid);

        List dbkey = new ArrayList();
        Map dbkeyMap = new HashMap();
        List masterList = new ArrayList();

        Map masterMap = new HashMap();
        masterMap.put("ip", ip);
        masterMap.put("port", port);
        masterList.add(masterMap);

        Map attachMap = new HashMap();
        attachMap.put("timeout", timeout);
        attachMap.put("read_timeout", readtimeout);

        dbkeyMap.put("master", masterList);
        dbkeyMap.put("attach", attachMap);

        map.put("dbkey",dbkeyMap);

        String content = JSON.toJSONString(map);
        String md5     = DigestUtils.md5Hex(content);
        logger.info(content);

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create   = simpleDateFormat.format(date);
        String gmt_modified = gmt_create;

        try{
            // 更新表appname_info
            jdbcTemplate.update("INSERT INTO appname_info(appname, groupname, type, created_time, modified_time) " +
                    "VALUE (?,?,?,?,?)", dataid,groupid,type,gmt_create,gmt_create);

            // 更新groupname_info_redis
            jdbcTemplate.update("INSERT INTO groupname_info_redis (groupname, timeout, read_timeout, " +
                    "role, ip, port, created_time, modified_time) " +
                    "VALUES(?,?,?,?,?,?,?,?)",
                    groupid,timeout,readtimeout,"Master",ip, port, gmt_create, gmt_modified);

            // 更新config_info 表
            String sql = "INSERT INTO config_info (data_id,group_id,content,md5,gmt_create,gmt_modified)" +
                    "VALUES(?,?,?,?,?,?)";
            jdbcTemplate.update(sql,dataid,groupid,content,md5,gmt_create,gmt_modified);

            msg.put("status", 200);
            msg.put("msg", "ok");
        }catch (DataAccessException e){
            logger.error(e);
            msg.put("status", 201);
            msg.put("msg", "更新数据库失败!" + e);
        }

        return msg;
    }

    @Override
    public Integer delConf() {
        return null;
    }

    @Override
    public Integer modifyConf() {
        return null;
    }

    @Override
    public Integer checkConf() {
        return null;
    }

    @Override
    public Map getConf(String dataid) {
        Map map = new HashMap();
        List redisList = configService.getConf(dataid);
        logger.info(redisList);
        Map tmpMap = listToMap.redisListToMap(redisList);
        logger.info(tmpMap);


        if (redisList.size()>0){
            map.put("status",        200);
            map.put("msg",           "ok");
            map.put("item_content",  tmpMap);
        }else{
            map.put("status",       202);
            map.put("msg",          "未查到该记录");
            map.put("item_content", tmpMap);
        }

        return map;
    }

    @Override
    public Map getConf(String dataid, String groupid) {
        Map map = new HashMap();
        List redisList = configService.getConf(dataid);
        logger.info(redisList);
        return map;
    }
}
