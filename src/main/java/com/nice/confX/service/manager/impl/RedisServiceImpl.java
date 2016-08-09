package com.nice.confX.service.manager.impl;

import com.alibaba.fastjson.JSON;
import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.ListToMap;
import com.nice.confX.utils.OtherUtil;
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

        OtherUtil util = new OtherUtil();
        Map map = util.setRedisInfo(request);

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
        List masterList = (List) ((Map) map.get("dbkey")).get("master");
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

    @Transactional
    @Override
    public void modifyConf(HttpServletRequest request) throws Exception {
        String appname     = request.getParameter("pappname");
        String groupname   = request.getParameter("pgroupname");
        String timeout     = request.getParameter("ptimeout");
        String readtimeout = request.getParameter("preadtimeout");

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create   = simpleDateFormat.format(date);
        String gmt_modified = gmt_create;

        //清理groupname_info_redis表
        String sql1 = "DELETE FROM groupname_info_redis " +
                "WHERE appname=? AND groupname=?";
        jdbcTemplate.update(sql1, appname, groupname);

        //清理config_info表
        String sql2 = "DELETE FROM config_info " +
                "WHERE data_id=? AND group_id=?";
        jdbcTemplate.update(sql2, appname, groupname);

        //新增groupname_info_redis表相关信息
        OtherUtil util = new OtherUtil();
        Map map = util.setRedisInfo(request);

        List masterList = (List) ((Map) map.get("dbkey")).get("master");
        String sql3 = "INSERT INTO " +
                "groupname_info_redis(" +
                "appname, groupname, timeout, read_timeout," +
                "role, ip, port, created_time, modified_time)" +
                " VALUES(?,?,?,?,?,?,?,?,?)";
        for (int j=0; j<masterList.size(); j++){
            String ipx   = (String) ((Map)masterList.get(j)).get("ip");
            String portx = (String) ((Map)masterList.get(j)).get("port");
            jdbcTemplate.update(
                    sql3,appname,groupname,timeout,
                    readtimeout,"Master",ipx, portx,
                    gmt_create, gmt_modified);
        }

        //新增config_info表相关信息
        String content = JSON.toJSONString(map);
        String md5     = DigestUtils.md5Hex(content);
        String sql4 = "INSERT INTO config_info(" +
                "data_id,group_id,content,md5,gmt_create,gmt_modified)" +
                "VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(sql4,appname,groupname,content,md5,gmt_create,gmt_modified);
    }

    @Override
    public Map checkConf(String appname, String groupname) throws Exception {
        return null;
    }

    @Override
    public Map getConf(String dataid) {
        List redisList = configService.getConf(dataid);
        logger.info(redisList);

        OtherUtil util = new OtherUtil();
        return util.genRedisResMap(redisList);
    }

    @Override
    public Map getConf(String dataid, String groupid) {
        List redisList = configService.getConf(dataid, groupid);
        logger.info(redisList);
        OtherUtil util = new OtherUtil();
        return util.genRedisResMap(redisList);
    }
}
