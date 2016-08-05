package com.nice.confX.service.manager.impl;

import com.alibaba.fastjson.JSON;
import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.ListToMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by yxb on 16/7/28.
 */
@Service(value="redis")
public class RedisServiceImpl implements MngService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigService configService;

    ListToMap listToMap = new ListToMap();

    private Logger logger = Logger.getLogger(RedisServiceImpl.class);

    @Transactional
    @Override
    public void addConf(HttpServletRequest request) throws Exception {
        String type        = request.getParameter("ptype");
        String dataid      = request.getParameter("pappname");
        String groupid     = request.getParameter("pgroupname");
        String timeout     = request.getParameter("ptimeout");
        String readtimeout = request.getParameter("preadtimeout");
        String ipport      = request.getParameter("pmiport");

        String[] iportx = ipport.trim().split(",");


        String[] iport = ipport.trim().split(":");
        String ip   = iport[0];
        String port = iport[1];

        List masterList = new ArrayList();
        for (int i=0; i<iportx.length; i++){
            String[] strarr = iportx[i].trim().split(":");
            Map tmpMap = new HashMap();
            tmpMap.put("ip",   strarr[0].trim());
            tmpMap.put("port", strarr[1].trim());

            masterList.add(tmpMap);
        }

        Map map = new HashMap();
        map.put("dataid",  dataid);
        map.put("groupid", groupid);

        List dbkey = new ArrayList();
        Map dbkeyMap = new HashMap();

//      Map masterMap = new HashMap();
//        masterMap.put("ip", ip);
//        masterMap.put("port", port);
//        masterList.add(masterMap);

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

        // 更新表appname_info
        jdbcTemplate.update("INSERT INTO appname_info(" +
                "appname, groupname, type, created_time, modified_time) " +
                "VALUE (?,?,?,?,?)",
                dataid,groupid,type,gmt_create,gmt_create);

        // 更新groupname_info_redis
        String sql2 = "INSERT INTO " +
                "groupname_info_redis(" +
                "appname, groupname, timeout, read_timeout," +
                "role, ip, port, created_time, modified_time)" +
                " VALUES(?,?,?,?,?,?,?,?,?)";
        for (int j=0; j<masterList.size(); j++){
            String ipx   = (String) ((Map)masterList.get(j)).get("ip");
            String portx = (String) ((Map)masterList.get(j)).get("port");
            jdbcTemplate.update(
                    sql2,dataid,groupid,timeout,
                    readtimeout,"Master",ipx, portx,
                    gmt_create, gmt_modified);

        }

        // 更新config_info 表
        String sql3 = "INSERT INTO config_info(" +
                "data_id,group_id,content,md5,gmt_create,gmt_modified)" +
                "VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(sql3,dataid,groupid,content,md5,gmt_create,gmt_modified);
    }

    @Override
    @Transactional
    public void delConf(String appname, String groupname, String type) throws Exception {
        // 清理appname_info表相关信息
        String sql1 = "DELETE FROM appname_info WHERE appname=? AND groupname=? AND type=?";
        jdbcTemplate.update(sql1, appname, groupname, type);

        // 清理groupname_info_redis表相关信息
        String sql2 = "DELETE FROM groupname_info_redis WHERE appname=? AND groupname=?";
        jdbcTemplate.update(sql2, appname, groupname);

        // 清理config_info表相关信息
        String sql3 = "DELETE FROM config_info WHERE data_id=? AND group_id=?";
        jdbcTemplate.update(sql3, appname, groupname);
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
