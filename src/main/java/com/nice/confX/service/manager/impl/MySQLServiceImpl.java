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

    @Override
    @Transactional
    public void copyConf(HttpServletRequest request) throws Exception {
        Map resMap = copycheck(request);

        if (resMap.get("code").equals("200")){
            logger.debug("check ok!");
            this.copyconf(request);
            // copy conf

        }else{
            logger.debug("check failed!");
        }




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


    // 复制某一个group
    @Transactional
    private Map checkCopy(String fprogram,   String fappname,
                          String fgroupname, String ftype,
                          String tprogram,   String tappname,
                          String ttype
    ) throws Exception {
        Map resMap = new HashMap();

        String sqlf = "SELECT count(1) AS cnt " +
                "FROM  appname_info " +
                "WHERE pname=? AND appname=? AND groupname=? AND type=?";

        List<Map<String, Object>> rsf = jdbcTemplate.queryForList(sqlf,fprogram,fappname,fgroupname,ftype);
        Number numberf = (Number) rsf.get(0).get("cnt");
        int cntf = numberf.intValue();

        String sqlt = "SELECT count(1) AS cnt " +
                "FROM project_info " +
                "WHERE pname=? AND pcode=? AND ptype=?";

        List<Map<String, Object>> rst = jdbcTemplate.queryForList(sqlt,tprogram,tappname,ttype);
        Number numbert = (Number) rst.get(0).get("cnt");
        int cntt = numbert.intValue();

        if (cntf <= 0) {
            String err = "From项目,工程名:"+fprogram+" 项目编码:"+fappname + " type:"+ftype+" 不存在";
            logger.error(err);

            resMap.put("code", "201");
            resMap.put("msg", "From项目|工程名不存在");
            resMap.put("data", err);

            return resMap;
        }

        if(cntt <= 0) {
            String err = "To项目,工程名:"+tprogram+" 项目编码:"+tappname + " type:"+ttype+" 不存在";
            logger.error(err);

            resMap.put("code", "202");
            resMap.put("msg", "To项目|工程名不存在");
            resMap.put("data", err);

            return resMap;
        }

        // 如果To中存在From的group,则不能复制
        String sql = "SELECT groupname " +
                "FROM appname_info " +
                "WHERE pname=? AND appname=? AND type=? AND groupname=?";
        List rs = jdbcTemplate.queryForList(sql, tprogram, tappname,ttype, fgroupname);


        if (rs.isEmpty()){
            resMap.put("code", "200");
            resMap.put("msg", "OK");
            resMap.put("data", "");

            return resMap;

        }else {
            resMap.put("code", "203");
            resMap.put("msg", "存在冲突,不能复制");
            resMap.put("data", rs);

            return resMap;
        }
    }

    // 整个项目复制
    @Transactional
    private Map checkCopy(String fprogram, String fappname,
                          String ftype,
                          String tprogram, String tappname,
                          String ttype
    ) throws Exception{
        Map resMap = new HashMap();

        String sql = "SELECT count(1) AS cnt " +
                "FROM  appname_info " +
                "WHERE pname=? AND appname=? AND type=?";

        List<Map<String, Object>> rsf = jdbcTemplate.queryForList(sql,fprogram,fappname,ftype);
        Number number = (Number) rsf.get(0).get("cnt");
        int cntf = number.intValue();

        String sqlt = "SELECT count(1) AS cnt " +
                "FROM project_info " +
                "WHERE pname=? AND pcode=? AND ptype=?";

        List<Map<String, Object>> rst = jdbcTemplate.queryForList(sqlt,tprogram,tappname,ttype);
        Number numbert = (Number) rst.get(0).get("cnt");
        int cntt = numbert.intValue();

        if (cntf <= 0) {
            String err = "From项目,工程名:"+fprogram+" 项目编码:"+fappname + " type:"+ftype+" 不存在";
            logger.error(err);

            resMap.put("code", "201");
            resMap.put("msg","From项目不存在");
            resMap.put("data", err);

            return resMap;
            //throw new Exception(err);
        }

        if(cntt <= 0) {
            String err = "To项目,工程名:"+tprogram+" 项目编码:"+tappname + " type:"+ttype+" 不存在";
            logger.error(err);

            resMap.put("code", "202");
            resMap.put("msg","To项目不存在");
            resMap.put("data", err);

            return resMap;
            //throw new Exception(err);
        }

        // 如果To项目中已经存在相应的group,则不能复制,需要停止;
        // select groupname from appname_info where pname='conf_ice_prod_sm' and appname='online_mysql1' and type='MySQL' and groupname in (select groupname from appname_info where pname='conf_ice_prod_sm' and appname='online_mysql' and type='MySQL');
        String sqlfgroup = "SELECT groupname " +
                "FROM appname_info " +
                "WHERE pname=? AND appname=? AND type=? " +
                "AND groupname in " +
                "(select groupname from appname_info where pname=? and appname=? and type=?)";

        List rs = jdbcTemplate.queryForList(sqlfgroup,tprogram,tappname,ttype,fprogram,fappname,ftype);

        // 如果rs为空,则可以进行拷贝,不为空,则表示有冲突
        if (rs.isEmpty()) {
            resMap.put("code", "200");
            resMap.put("msg", "Ok");
            resMap.put("data", "");
            return resMap;
        }else {
            resMap.put("code", "203");
            resMap.put("msg", "groupname有冲突,不能复制");
            resMap.put("data", rs);

            return resMap;
        }
    }

    private Map copycheck(HttpServletRequest request) throws Exception{
        String fprogram = request.getParameter("fprogramname");
        String fappname = request.getParameter("fappname");
        String ftype    = request.getParameter("ftype");
        String tprogram = request.getParameter("tprogramname");
        String tappname = request.getParameter("tappname");
        String ttype    = ftype;

        String fgroupname = request.getParameter("fgroupname");

        if (    fprogram == null || fprogram.equals("") ||
                fappname == null || fappname.equals("") ||
                ftype    == null || ftype.equals("")    ||
                tprogram == null || tprogram.equals("") ||
                tappname == null || tappname.equals("") ||
                ttype    == null || ttype.equals("")
                )
        {
            String err = "部分输入参数为空,请检查." +
                    " fprogram:" + fprogram +
                    " fappname:" + fappname +
                    " ftype:"    + ftype +
                    " tprogram:" + tprogram +
                    " tappname:" + tappname;
            logger.error(err);

            throw new Exception(err);
        }

        /**
         *  检测from 和 to 的工程及项目编号是否存在, 如果存在则ok, 否则不能进行拷贝。
         * */
        Map map = new HashMap();
        // fgroupname 为空的话,就是复制所有group的数据,否则只覆盖指定的group数据
        if (fgroupname == null || fgroupname.equals("")){


            // 需要check目标项目中,group是否已经存在,如果存在则为了防止发生覆盖,需要进行check,如果有
            // 重复的,则不能复制

            map = this.checkCopy(fprogram,fappname,ftype,tprogram,tappname,ttype);

        }else {

            map = this.checkCopy(fprogram,fappname,fgroupname,ftype, tprogram, tappname, ttype);
        }

        return map;

    }


    @Transactional
    private void copyconf(HttpServletRequest request) throws Exception{
        String fprogram   = request.getParameter("fprogramname");
        String fappname   = request.getParameter("fappname");
        String fgroupname = request.getParameter("fgroupname");
        String ftype      = request.getParameter("ftype");
        String tprogram   = request.getParameter("tprogramname");
        String tappname   = request.getParameter("tappname");
        String ttype      = ftype;

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_created = simpleDateFormat.format(date);
        String gmt_modified = gmt_created;

        // 1. 从表appname_info中查出From的信息,然后To的要复制一份该信息
        String sql_insert_appname = "INSERT INTO " +
                "appname_info(pname,appname,groupname,type,created_time,modified_time) " +
                "VALUES(?,?,?,?,?,?)";

        // 2.  根据类型判断是写 groupname_info_mysql|groupname_info_redis表
        String sql_insert_groupname_info_mysql = "INSERT INTO " +
                "groupname_info_mysql (appname, pname, groupname, dbname, role, ip, port, " +
                "user, passwd, charset, tbprefix, timeout, created_time, modified_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String sql_insert_groupname_info_redis = "INSERT INTO " +
                "groupname_info_redis (appname, pname, groupname, timeout, read_timeout, " +
                "role, ip, port, created_time, modified_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";

        String sql_groupname_mysql = "SELECT dbname, role, ip, port," +
                "user, passwd,charset, tbprefix, timeout " +
                "FROM groupname_info_mysql WHERE appname=? AND pname=? AND groupname=?";


        // 3. 生成对应的内容,写入表config_info中
        String sql_config_info = "INSERT INTO config_info (program_id,data_id,type,group_id,content,md5,gmt_create, gmt_modified) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        // 全部拷贝
        if (fgroupname == null || fgroupname.equals("")){

            String sql1 = "SELECT groupname FROM appname_info WHERE pname=? AND appname=? AND type=?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1, fprogram, fappname, ftype);

            for (int i=0; i< list.size(); i++){
                String groupname = list.get(i).get("groupname").toString();
                jdbcTemplate.update(sql_insert_appname, tprogram, tappname,
                        groupname,ttype, gmt_created, gmt_modified);

                if (ftype.equals("MySQL")){
                    List<Map<String, Object>> mysqllist =
                            jdbcTemplate.queryForList(sql_groupname_mysql,fappname,fprogram,groupname);

                    updateMysqlinfo(mysqllist,tprogram,tappname,groupname);

                }else if(ftype.equals("Redis")){

                }
            }

        } else {
            jdbcTemplate.update(sql_insert_appname, tprogram, tappname,
                    fgroupname, ttype, gmt_created, gmt_modified);

            if (ftype.equals("MySQL")){
                List<Map<String, Object>> mysqllist =
                        jdbcTemplate.queryForList(sql_groupname_mysql, fappname, fprogram, fgroupname);
                updateMysqlinfo(mysqllist,tprogram,tappname,fgroupname);

            }else if(ftype.equals("Redis")){

            }

        }
    }

    @Transactional
    private void updateMysqlinfo(List<Map<String, Object>> mysqllist, String programname,
                                 String appname, String groupname) throws Exception{

        String sql_insert_groupname_info_mysql = "INSERT INTO " +
                "groupname_info_mysql (appname, pname, groupname, dbname, role, ip, port, " +
                "user, passwd, charset, tbprefix, timeout, created_time, modified_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String sql_config_info = "INSERT INTO config_info (program_id,data_id,type,group_id,content,md5,gmt_create, gmt_modified) " +
                "VALUES(?,?,?,?,?,?,?,?)";

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_created = simpleDateFormat.format(date);
        String gmt_modified = gmt_created;

        for (int k=0; k<mysqllist.size(); k++){
            jdbcTemplate.update(sql_insert_groupname_info_mysql, appname, programname,
                    groupname,
                    mysqllist.get(k).get("dbname"),
                    mysqllist.get(k).get("role"),
                    mysqllist.get(k).get("ip"),
                    mysqllist.get(k).get("port"),
                    mysqllist.get(k).get("user"),
                    mysqllist.get(k).get("passwd"),
                    mysqllist.get(k).get("charset"),
                    mysqllist.get(k).get("tbprefix"),
                    mysqllist.get(k).get("timeout"),
                    gmt_created, gmt_modified
            );
        }

        String dbname   = mysqllist.get(0).get("dbname").toString();
        String tbprefix = mysqllist.get(0).get("tbprefix").toString();
        String username = mysqllist.get(0).get("user").toString();
        String passwd   = mysqllist.get(0).get("passwd").toString();
        String charset  = mysqllist.get(0).get("charset").toString();
        String timeout  = mysqllist.get(0).get("timeout").toString();
        String type     = "MySQL";

        String masterstr = "";
        String slavestr  = "";

        for (int k=0;k<mysqllist.size();k++){
            if ( mysqllist.get(k).get("role").toString().equalsIgnoreCase("master") ){
                masterstr += mysqllist.get(k).get("ip").toString()+":"+mysqllist.get(k).get("port").toString();
                masterstr += ",";

            }else if(mysqllist.get(k).get("role").toString().equalsIgnoreCase("slave") ) {
                slavestr += mysqllist.get(k).get("ip").toString()+":"+mysqllist.get(k).get("port").toString();
                slavestr += ",";
            }
        }

        String[] master = masterstr.trim().split(",");
        String[] slaver = slavestr.trim().split(",");
        OtherUtil util = new OtherUtil();
        Map map = util.setMysqlInfo(appname,groupname,dbname,tbprefix,username,
                passwd,charset,timeout,master,slaver);

        String content = JSON.toJSONString(map);
        String md5 = DigestUtils.md5Hex(content);

        jdbcTemplate.update(sql_config_info,programname,appname,
                type,groupname,content,md5,gmt_created,gmt_modified);

    }
}

