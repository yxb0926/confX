package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.MySQLService;
import com.nice.confX.utils.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer addConf(HttpServletRequest request) {

        String dataid = request.getParameter("pcode");
        String groupid = request.getParameter("pgroupname");
        String dbname  = request.getParameter("pdbname");

        JSONObject jsonObject = new JSONObject();
        JSONArray  masterarr  = new JSONArray();
        JSONArray  slavearr   = new JSONArray();

        JSONObject mobj = new JSONObject();
        mobj.put("ip",   request.getParameter("pmip"));
        mobj.put("port", request.getParameter("pmport"));
        mobj.put("user", request.getParameter("pmuser"));
        mobj.put("passwd", request.getParameter("pmpass"));

        masterarr.put(mobj);

        JSONObject sobj = new JSONObject();
        sobj.put("ip",      request.getParameter("psip"));
        sobj.put("port",    request.getParameter("psport"));
        sobj.put("user",    request.getParameter("psuser"));
        sobj.put("passwd",  request.getParameter("pspass"));

        slavearr.put(sobj);

        jsonObject.put("master", masterarr);
        jsonObject.put("slave", slavearr);
        jsonObject.put("dbname", request.getParameter("pdbname"));
        jsonObject.put("tbprefix", request.getParameter("ptbprefix"));
        jsonObject.put("charset", request.getParameter("pcharsetx"));
        jsonObject.put("timeout", request.getParameter("ptimeoutx"));

        String md5 = DigestUtils.md5Hex(jsonObject.toString());

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_create = simpleDateFormat.format(date);


        try {
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
    public List getAllMyConf(String dataid) {
        List myList  = new ArrayList();
        List pmyList = new ArrayList();
        try {
            myList = jdbcTemplate.queryForList("SELECT data_id, group_id, dbname, content " +
                    "FROM config_info WHERE data_id=?", dataid);
        }catch (DataAccessException e){
            System.out.println(e);
            return null;
        }

        JsonUtil jsonUtil = new JsonUtil();
        if (myList.size()>0){
            for (int i=0; i<myList.size(); i++){
                Map myMap = (Map) myList.get(i);

                String myConent = myMap.get("content").toString();

                /**
                 * 将content内容转换为一个hashmap
                 * */
                HashMap msMap = jsonUtil.jsonToMap(myConent);
                msMap.put("group_id", myMap.get("group_id"));
                msMap.put("data_id",  myMap.get("data_id"));

                pmyList.add(msMap);
            }
        }
        return pmyList;
    }

    @Override
    public List getOneMyConf(String dataid, String groupid) {
        List myList  = new ArrayList();
        List pmyList = new ArrayList();

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
                "  AND group_id in (" + group_id +
                ")";


        try{
            myList = jdbcTemplate.queryForList(sql);
        }catch (DataAccessException e){
            System.out.println(e);
            return null;
        }

        JsonUtil jsonUtil = new JsonUtil();
        if (myList.size()>0){
            for (int i=0; i<myList.size(); i++){
                Map myMap = (Map) myList.get(i);

                String myConent = myMap.get("content").toString();

                /**
                 * 将content内容转换为一个hashmap
                 * */
                HashMap msMap = jsonUtil.jsonToMap(myConent);
                msMap.put("group_id", myMap.get("group_id"));
                msMap.put("data_id",  myMap.get("data_id"));

                pmyList.add(msMap);
            }
        }
        return pmyList;
    }
}
