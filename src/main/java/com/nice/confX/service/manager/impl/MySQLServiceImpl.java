package com.nice.confX.service.manager.impl;

import com.alibaba.fastjson.JSON;
import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.OtherUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Exchanger;


/**
 * Created by yxb on 16/7/5.
 */
@Service("mysql")
public class MySQLServiceImpl implements MngService {

    private Logger logger = Logger.getLogger(MySQLServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigService configService;

    /**
     * 2016-7-14
     * 3 个表都增加成功才算成功, 这里有事务请注意
     * appname_info 表
     * groupname_info 表
     * project_info 表
     * */
    @Transactional
    @Override
    public void addConf(HttpServletRequest request) throws Exception {
        String dataid   = request.getParameter("pappname").trim(); // pcode, appname
        String pname    = request.getParameter("pname").trim();     // program name
        String groupid  = request.getParameter("pgroupname").trim();
        String dbname   = request.getParameter("pdbname").trim();
        String tbprefix = request.getParameter("ptbprefix").trim();
        String username = request.getParameter("puser").trim();
        String passwd   = request.getParameter("ppass").trim();
        String charset  = request.getParameter("pcharsetx").trim();
        String timeout  = request.getParameter("ptimeoutx").trim();
        String type     = request.getParameter("ptype").trim();
        String[] masters = request.getParameter("pmiport").trim().split(",");
        String[] slavers = request.getParameter("psiport").trim().split(",");

        OtherUtil util = new OtherUtil();
        //Map map = util.setMysqlInfo(request);
        Map map = util.setMysqlInfo(dataid, groupid, dbname, tbprefix,
                username, passwd, charset, timeout, masters, slavers);

        String content = JSON.toJSONString(map);
        String md5 = DigestUtils.md5Hex(content);

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create = simpleDateFormat.format(date);

        // 更新表appname_info
        String sql = "INSERT INTO appname_info " +
                "(appname, pname, groupname, type, created_time, modified_time) " +
                "VALUE (?,?,?,?,?,?)";
        jdbcTemplate.update(sql,dataid,pname,groupid,type,gmt_create,gmt_create);

        // 新增master
        Map mmap = (Map) ((List) ((Map) map.get("dbkey")).get("master")).get(0);
        String mip   = mmap.get("ip").toString();
        String mport = mmap.get("port").toString();
        jdbcTemplate.update("INSERT INTO groupname_info_mysql(" +
                "appname,pname,groupname,dbname,role,ip,port," +
                "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                "VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                dataid, pname, groupid, dbname, "master",
                mip,mport,username,passwd,charset,
                tbprefix,timeout,gmt_create,gmt_create);

        // 新增slave
        List slavearr = (List) ((Map) map.get("dbkey")).get("slave");
        for (int i=0; i<slavearr.size(); i++){
            Map smap = (Map) slavearr.get(i);
            String sip   = smap.get("ip").toString();
            String sport = smap.get("port").toString();
            jdbcTemplate.update("INSERT INTO groupname_info_mysql(" +
                    "appname,pname,groupname,dbname,role,ip,port," +
                    "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                    "VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    dataid, pname, groupid, dbname, "slave",
                    sip, sport, username,passwd,charset,
                    tbprefix, timeout, gmt_create, gmt_create);
        }

        // 更新表config_info
        jdbcTemplate.update("INSERT INTO config_info(" +
                "program_id, data_id, type, group_id,content,md5,gmt_create,gmt_modified) " +
                "VALUE (?,?,?,?,?,?,?,?)",
                pname, dataid, type, groupid, content, md5, gmt_create, gmt_create);
    }

    @Override
    @Transactional
    public void delConf(String appname, String pname, String groupname, String type) throws Exception{
        // 清理appname_info表相应信息
        String sql1 = "DELETE FROM appname_info WHERE pname=? AND appname=? AND groupname=? AND type=?";
        jdbcTemplate.update(sql1, pname, appname, groupname, type);

        // 清理groupname_info_mysql表相应信息
        String sql2 = "DELETE FROM groupname_info_mysql WHERE pname=? AND appname=? AND groupname=?";
        jdbcTemplate.update(sql2, pname, appname, groupname);

        // 清理config_info表相应信息
        String sql3 = "DELETE FROM config_info WHERE program_id=? AND data_id=? AND group_id=? AND type=?";
        jdbcTemplate.update(sql3, pname, appname, groupname, type);

    }

    @Override
    @Transactional
    public void modifyConf(HttpServletRequest request) throws Exception {
        /**
         *  修改逻辑:
         *  1. appname、groupname和dbname是不能修改的;
         *  2. 修改部分主要是dbkey部分,即ip端口、密码、超时时间等;
         *  3. 保证事务,修改前要清除掉老的数据,然后添加新的数据;
         *  4. 清理表
         *     a) groupname_info_mysql;
         *     b) config_info
         *
         *  5. 添加
         *     a) groupname_info_mysql;
         *     b) config_info
         * */

        String dataid    = request.getParameter("pappname").trim(); // pcode, appname
        String pname     = request.getParameter("pname").trim();    // program name
        String ptype     = request.getParameter("ptype").trim();
        String groupid   = request.getParameter("pgroupname").trim();
        String dbname    = request.getParameter("pdbname").trim();
        String tbprefix  = request.getParameter("ptbprefix").trim();
        String username  = request.getParameter("puser").trim();
        String passwd    = request.getParameter("ppass").trim();
        String charset   = request.getParameter("pcharsetx").trim();
        String timeout   = request.getParameter("ptimeoutx").trim();
        String appname   = request.getParameter("pappname").trim();
        String groupname = request.getParameter("pgroupname").trim();
        String[] masters = request.getParameter("pmiport").trim().split(",");
        String[] slavers = request.getParameter("psiport").trim().split(",");

        OtherUtil util = new OtherUtil();
        //Map map = util.setMysqlInfo(request);

        Map map = util.setMysqlInfo(dataid, groupid, dbname, tbprefix,
                username, passwd, charset, timeout, masters, slavers);
        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create = simpleDateFormat.format(date);

        // 清理groupname_info_mysql表相应数据
        String sql1 = "DELETE FROM groupname_info_mysql " +
                "WHERE appname=? AND pname=? AND groupname=? AND dbname=?";
        jdbcTemplate.update(sql1, appname, pname, groupname, dbname);

        // 清理config_info表相应数据
        String sql2 = "DELETE FROM config_info " +
                "WHERE program_id=? AND  data_id=? AND group_id=? AND type=?";
        jdbcTemplate.update(sql2, pname, appname, groupname, ptype);

        // 增加groupname_info_mysql表相应数据
        // 新增master
        Map mmap = (Map) ((List) ((Map) map.get("dbkey")).get("master")).get(0);
        String mip   = mmap.get("ip").toString();
        String mport = mmap.get("port").toString();
        jdbcTemplate.update("INSERT INTO groupname_info_mysql(" +
                        "pname, appname,groupname,dbname,role,ip,port," +
                        "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                        "VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                pname, dataid, groupid, dbname, "master",
                mip,mport,username,passwd,charset,
                tbprefix,timeout,gmt_create,gmt_create);

        // 新增slave
        List slavearr = (List) ((Map) map.get("dbkey")).get("slave");
        for (int i=0; i<slavearr.size(); i++){
            Map smap = (Map) slavearr.get(i);
            String sip   = smap.get("ip").toString();
            String sport = smap.get("port").toString();
            jdbcTemplate.update("INSERT INTO groupname_info_mysql(" +
                            "pname,appname,groupname,dbname,role,ip,port," +
                            "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                            "VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    pname, dataid, groupid, dbname, "slave",
                    sip, sport, username,passwd,charset,
                    tbprefix, timeout, gmt_create, gmt_create);
        }

        String content = JSON.toJSONString(map);
        String md5 = DigestUtils.md5Hex(content);

        // 增加config_info表相应数据
        jdbcTemplate.update("INSERT INTO config_info(" +
                        "program_id,data_id,type,group_id,content,md5,gmt_create,gmt_modified) " +
                        "VALUE (?,?,?,?,?,?,?,?)",
                pname, dataid, ptype, groupid, content, md5, gmt_create, gmt_create);
    }

    @Override
    public Map checkConf(String appname, String pname, String groupname, String type) throws Exception {
        Map map = new HashMap();
        Map resMap = new HashMap();
        List myList = configService.getConf(appname, pname, groupname, type);
        logger.debug(myList);

        resMap = check(myList);

        map.put("status",  200);
        map.put("data", resMap);
        return map;
    }

    @Override
    public Map checkConf(String appname, String pname, String type) throws Exception {
        Map map = new HashMap();
        Map resMap = new HashMap();
        List myList = configService.getConf(appname, pname, type);
        logger.debug(myList);

        resMap = check(myList);

        map.put("status",  200);
        map.put("data", resMap);
        return map;
    }

    @Override
    public Map getConf(String dataid, String pname, String type) {
        List myList = configService.getConf(dataid, pname, type);
        logger.debug(myList);
        OtherUtil util = new OtherUtil();

        return util.genResMap(myList);

    }

    @Override
    public Map getConf(String dataid, String pname, String groupid, String type) {
        List myList = configService.getConf(dataid, pname, groupid, type);
        logger.debug(myList);
        OtherUtil util = new OtherUtil();

        return util.genResMap(myList);
    }

    private Map check(List myList){
        Map resMap = new HashMap();

        OtherUtil util = new OtherUtil();
        Map itemMap = (Map) util.genResMap(myList).get("item_content");
        for ( Object tmpMap : itemMap.values()){
            Map mmap = (Map) tmpMap;
            Map contentMap   = (Map) mmap.get("content");
            Map dbkeyMap     = (Map) contentMap.get("dbkey");
            String groupid   = contentMap.get("groupid").toString();
            List masterList  = (List) dbkeyMap.get("master");
            List slaveList   = (List) dbkeyMap.get("slave");
            Map  attachMap   = (Map) dbkeyMap.get("attach");

            String dbname   = contentMap.get("dbname").toString();
            String username = attachMap.get("user").toString();
            String passwd   = attachMap.get("passwd").toString();

            Map mMap = util.pingMysql(masterList,username,passwd,dbname);
            Map sMap = util.pingMysql(slaveList, username,passwd,dbname);
            Map msMap = new HashMap();
            msMap.put("master", mMap);
            msMap.put("slave",  sMap);

            resMap.put(groupid, msMap);
        }

        return resMap;
    }
}

