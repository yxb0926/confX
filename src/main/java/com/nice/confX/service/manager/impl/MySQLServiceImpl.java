package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.ConfigService;
import com.nice.confX.service.manager.MngService;
import com.nice.confX.utils.ListToMap;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;



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

    ListToMap listToMap = new ListToMap();
    /**
     * 2016-7-14
     * 3 个表都增加成功才算成功, 这里有事务请注意
     * appname_info 表
     * groupname_info 表
     * project_info 表
     * */
    @Transactional
    @Override
    public Map addConf(HttpServletRequest request) throws Exception {

        Map msg = new HashMap();
        msg.put("status", 201);
        msg.put("msg", "");

        String dataid   = request.getParameter("pappname").trim(); // pcode, appname
        String groupid  = request.getParameter("pgroupname").trim();
        String dbname   = request.getParameter("pdbname").trim();
        String tbprefix = request.getParameter("ptbprefix").trim();
        String username = request.getParameter("puser").trim();
        String passwd   = request.getParameter("ppass").trim();
        String charset  = request.getParameter("pcharsetx").trim();
        String timeout  = request.getParameter("ptimeoutx").trim();
        String masterx  = request.getParameter("pmiport").trim();
        String slaverx  = request.getParameter("psiport").trim();
        String type     = request.getParameter("ptype");
        String[] master = masterx.split(",");
        String[] slaver = slaverx.split(",");

        JSONObject jsonObject = new JSONObject();
        JSONArray  masterarr  = new JSONArray();
        JSONArray  slavearr   = new JSONArray();
        JSONArray  hostarr    = new JSONArray();

        /**
         *  Master  只允许1个,不允许多个
         * */

        if ( master.length !=  1 ){
            msg.put("msg", "Master不允许多个!");
            return msg;
        }

        /**
         *  解析Master信息
         * */
        String[] mip_port = master[0].trim().split(":");
        JSONObject mobj = new JSONObject();
        mobj.put("ip",   mip_port[0].trim());
        mobj.put("port", mip_port[1].trim());

        masterarr.put(mobj);

        /**
         *  解析Slave信息
         * */
        for (int j=0; j< slaver.length; j++){
            String[] sip_port = slaver[j].trim().split(":");

            JSONObject sobj = new JSONObject();
            sobj.put("ip",     sip_port[0].trim());
            sobj.put("port",   sip_port[1].trim());
            slavearr.put(sobj);
        }

        JSONObject attachObj  = new JSONObject();
        attachObj.put("tbprefix", tbprefix);
        attachObj.put("charset",  charset);
        attachObj.put("timeout",  timeout);
        attachObj.put("user",     username);
        attachObj.put("passwd",   passwd);

        JSONObject hostObj  = new JSONObject();
        hostObj.put("master", masterarr);
        hostObj.put("slave",  slavearr);
        hostObj.put("attach", attachObj);


        jsonObject.put("dbname",  dbname);
        jsonObject.put("dbkey",   hostObj);
        jsonObject.put("groupid", groupid);
        jsonObject.put("dataid",  dataid);

        String md5 = DigestUtils.md5Hex(jsonObject.toString());

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create = simpleDateFormat.format(date);

        // 更新表appname_info
        jdbcTemplate.update("INSERT INTO appname_info(appname, groupname, type, created_time, modified_time) " +
                    "VALUE (?,?,?,?,?)", dataid,groupid,type,gmt_create,gmt_create);

        // 新增master
        jdbcTemplate.update("INSERT INTO groupname_info_mysql(groupname,dbname,role,ip,port," +
                    "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                    "VALUE (?,?,?,?,?,?,?,?,?,?,?,?)",groupid, dbname, "master",
                    mobj.get("ip"),mobj.get("port"),username,passwd,charset,
                    tbprefix,timeout,gmt_create,gmt_create);

        // 新增slave
        for (int i=0; i<slavearr.length(); i++){
            JSONObject tmpobj = (JSONObject) slavearr.get(i);
            jdbcTemplate.update("INSERT INTO groupname_info_mysql(groupname,dbname,role,ip,port," +
                        "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                        "VALUE (?,?,?,?,?,?,?,?,?,?,?,?)", groupid, dbname, "slave",
                        tmpobj.get("ip"), tmpobj.get("port"),username,passwd,charset,
                        tbprefix, timeout, gmt_create, gmt_create);
        }

        // 更新表config_info
        jdbcTemplate.update("INSERT INTO config_info(data_id,group_id,content,md5,gmt_create,gmt_modified) " +
                    "VALUE (?,?,?,?,?,?)", dataid, groupid, jsonObject.toString(), md5, gmt_create, gmt_create);


        msg.put("status", 200);
        msg.put("msg", "ok");
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
        Map myMap = new HashMap();
        List myList = configService.getConf(dataid);
        logger.info(myList);

        myMap = listToMap.mylistToMap(myList);


        if (myList.size()>0){
            map.put("status",        200);
            map.put("msg",           "ok");
            map.put("item_content",  myMap);
        }else{
            map.put("status",       202);
            map.put("msg",          "未查到该记录");
            map.put("item_content", myMap);
        }

        return map;
    }

    @Override
    public Map getConf(String dataid, String groupid) {
        List myList = configService.getConf(dataid, groupid);
        logger.info(myList);

        return listToMap.mylistToMap(myList);
    }
}

