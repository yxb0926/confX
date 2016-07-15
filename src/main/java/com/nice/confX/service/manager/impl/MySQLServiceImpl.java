package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.MySQLService;
import com.nice.confX.utils.JsonUtil;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * Created by yxb on 16/7/5.
 */
@Service
public class MySQLServiceImpl implements MySQLService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 2016-7-14
     * 3 个表都增加成功才算成功, 这里有事务请注意
     * appname_info 表
     * groupname_info 表
     * project_info 表
     * */
    public Integer addConf(HttpServletRequest request) {

        String dataid   = request.getParameter("pcode").trim(); // pcode, appname
        String groupid  = request.getParameter("pgroupname").trim();
        String dbname   = request.getParameter("pdbname").trim();
        String tbprefix = request.getParameter("ptbprefix").trim();
        String username = request.getParameter("puser").trim();
        String passwd   = request.getParameter("ppass").trim();
        String charset  = request.getParameter("pcharsetx").trim();
        String timeout  = request.getParameter("ptimeoutx").trim();
        String masterx  = request.getParameter("pmiport").trim();
        String slaverx  = request.getParameter("psiport").trim();
        String type     = "MySQL";
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
            return 0;
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


        try {
            // 更新表appname_info
            jdbcTemplate.update("INSERT INTO appname_info(appname, groupname, type, created_time, modified_time) " +
                    "VALUE (?,?,?,?,?)", dataid,groupid,type,gmt_create,gmt_create);

            // 更新表groupname_info

            // 新增master
            jdbcTemplate.update("INSERT INTO groupname_info(groupname,dbname,role,ip,port," +
                    "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                    "VALUE (?,?,?,?,?,?,?,?,?,?,?,?)",groupid, dbname, "master",
                    mobj.get("ip"),mobj.get("port"),username,passwd,charset,
                    tbprefix,timeout,gmt_create,gmt_create);

            // 新增slave
            for (int i=0; i<slavearr.length(); i++){
                JSONObject tmpobj = (JSONObject) slavearr.get(i);
                jdbcTemplate.update("INSERT INTO groupname_info(groupname,dbname,role,ip,port," +
                        "user, passwd, charset, tbprefix, timeout,created_time, modified_time) " +
                        "VALUE (?,?,?,?,?,?,?,?,?,?,?,?)", groupid, dbname, "slave",
                        tmpobj.get("ip"), tmpobj.get("port"),username,passwd,charset,
                        tbprefix, timeout, gmt_create, gmt_create);
            }

            // 更新表config_info
            jdbcTemplate.update("INSERT INTO config_info(data_id,group_id,dbname,content,md5,gmt_create,gmt_modified) " +
                    "VALUE (?,?,?,?,?,?,?)", dataid, groupid, dbname, jsonObject.toString(), md5, gmt_create, gmt_create);
        }catch (DataAccessException e){
            System.out.println(e);
            return 0;
        }

        return 1;
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
    public Map getMyConf(String dataid) {
        List myList = null;
        List outList = new ArrayList();
        Map outMap = new HashMap();
        try {
            myList = jdbcTemplate.queryForList("SELECT data_id, group_id, dbname, content, md5 " +
                    "FROM config_info WHERE data_id=?", dataid);
        }catch (DataAccessException e){
            System.out.println(e);
            return null;
        }

        JsonUtil jsonUtil = new JsonUtil();
        if (myList.size()>0) {
            for (int i = 0; i < myList.size(); i++) {
                Map tmpMap = new HashMap();
                Map myMap = (Map) myList.get(i);

                String groupname = myMap.get("group_id").toString();
                String myConent = myMap.get("content").toString();
                tmpMap.put("content", jsonUtil.contentToMap(myConent));
                tmpMap.put("md5",     myMap.get("md5").toString());

                outMap.put(groupname, tmpMap);

            }
        }
        return outMap;
    }

    @Override
    public Map getMyConf(String dataid, String groupid) {
        List myList  = new ArrayList();
        List pmyList = new ArrayList();
        Map outMap = new HashMap();

        String[] groupidx = groupid.split("\\|");
        String group_id = "";
        for (int i = 0; i <groupidx.length; i++){
            group_id = group_id + "'" + groupidx[i] + "'";
            if (i < groupidx.length-1){
                group_id += ",";
            }
        }

        String sql = "select data_id, group_id, dbname, content FROM config_info " +
                "WHERE data_id=" + "'" + dataid + "'" +
                "  AND group_id in (" + group_id + ")";

        try{
            myList = jdbcTemplate.queryForList(sql);
        }catch (DataAccessException e){
            System.out.println(e);
            return null;
        }

        JsonUtil jsonUtil = new JsonUtil();
        if (myList.size()>0) {
            for (int i = 0; i < myList.size(); i++) {
                Map tmpMap = new HashMap();
                Map myMap = (Map) myList.get(i);

                String groupname = myMap.get("group_id").toString();
                String myConent = myMap.get("content").toString();
                tmpMap.put("content", jsonUtil.contentToMap(myConent));
                tmpMap.put("md5",     myMap.get("md5").toString());

                outMap.put(groupname, tmpMap);

            }
        }
        return outMap;
    }
}
