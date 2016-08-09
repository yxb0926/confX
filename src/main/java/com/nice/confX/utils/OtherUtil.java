package com.nice.confX.utils;


import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/8/5.
 */
public class OtherUtil {
    private Logger logger = Logger.getLogger(OtherUtil.class);

    public String listToString(List list){
        String ipstr = "";
        for (int i=0; i<list.size(); i++){
            Map map = (Map) list.get(i);
            ipstr += map.get("ip");
            ipstr += ":";
            ipstr += map.get("port");
            ipstr += ",";
            ipstr += "\n";
        }

        return ipstr;
    }

    public Map setMysqlInfo(HttpServletRequest request) throws Exception{
        Map map = new HashMap();
        System.out.println(request);

        String dataid   = request.getParameter("pappname").trim(); // pcode, appname
        String groupid  = request.getParameter("pgroupname").trim();
        String dbname   = request.getParameter("pdbname").trim();
        String tbprefix = request.getParameter("ptbprefix").trim();
        String username = request.getParameter("puser").trim();
        String passwd   = request.getParameter("ppass").trim();
        String charset  = request.getParameter("pcharsetx").trim();
        String timeout  = request.getParameter("ptimeoutx").trim();
        String[] master = request.getParameter("pmiport").trim().split(",");
        String[] slaver = request.getParameter("psiport").trim().split(",");

        List masterarr = new ArrayList();

        if (master.length != 1){
            throw new Exception("Master 只能有一个!");
        }

        /**
         * 解析Master信息
         * */
        String[] mip_port = master[0].trim().split(":");
        Map mmap = new HashMap();
        mmap.put("ip",   mip_port[0].trim());
        mmap.put("port", mip_port[1].trim());

        masterarr.add(mmap);

        /**
         * 解析Slave信息
         * */
        List slaverarr = this.getipportList(slaver);

        Map attachMap = new HashMap();
        attachMap.put("tbprefix", tbprefix);
        attachMap.put("charset",  charset);
        attachMap.put("timeout",  timeout);
        attachMap.put("user",     username);
        attachMap.put("passwd",   passwd);

        Map dbkeyMap = new HashMap();
        dbkeyMap.put("master",  masterarr);
        dbkeyMap.put("slave",   slaverarr);
        dbkeyMap.put("attach",  attachMap);


        map.put("dbname",  dbname);
        map.put("dbkey",   dbkeyMap);
        map.put("groupid", groupid);
        map.put("dataid",  dataid);

        return map;
    }

    public Map setRedisInfo(HttpServletRequest request) throws Exception{
        Map map = new HashMap();

        String dataid      = request.getParameter("pappname");
        String groupid     = request.getParameter("pgroupname");
        String timeout     = request.getParameter("ptimeout");
        String readtimeout = request.getParameter("preadtimeout");

        String[] masterArr = request.getParameter("pmiport").trim().split(",");

        List masterList = this.getipportList(masterArr);

        Map dbkeyMap = new HashMap();
        Map attachMap = new HashMap();

        dbkeyMap.put("master", masterList);
        dbkeyMap.put("attach", attachMap);

        attachMap.put("timeout", timeout);
        attachMap.put("read_timeout", readtimeout);

        map.put("dataid",  dataid);
        map.put("groupid", groupid);
        map.put("dbkey",   dbkeyMap);

        return map;
    }

    public Map genResMap(List myList){
        ListToMap listToMap = new ListToMap();
        Map map   = new HashMap();
        Map myMap = new HashMap();
        map.put("status",       200);
        map.put("msg",          "ok");
        map.put("item_content", myMap);

        if (myList.size()>0){
            myMap = listToMap.mylistToMap(myList);
            map.put("item_content",  myMap);
        }else {
            map.put("status",       202);
            map.put("msg",          "未查到该记录");
        }
        return map;
    }

    public Map genRedisResMap(List myList){
        ListToMap listToMap = new ListToMap();
        Map map   = new HashMap();
        Map myMap = new HashMap();
        map.put("status",       200);
        map.put("msg",          "ok");
        map.put("item_content", myMap);

        if (myList.size()>0){
            myMap = listToMap.redisListToMap(myList);
            map.put("item_content",  myMap);
        }else {
            map.put("status",       202);
            map.put("msg",          "未查到该记录");
        }
        return map;
    }

    private List getipportList(String[] strArr){
        List list = new ArrayList() ;
        for(int i=0; i<strArr.length; i++){
            String[] ipportStrArr = strArr[i].trim().split(":");
            Map tmpMap = new HashMap();
            tmpMap.put("ip",    ipportStrArr[0].trim());
            tmpMap.put("port",  ipportStrArr[1].trim());
            list.add(tmpMap);
        }
        return list;
    }

    public Map pingMysql(List list, String username, String passwd, String dbname){
        Map map = new HashMap();
        List resList = new ArrayList();

        String checkSql = "SELECT 1";
        for(int i=0; i< list.size(); i++){
            Map masterMap = (Map) list.get(i);
            String mip    = (String) masterMap.get("ip");
            String mport  = (String) masterMap.get("port");

            try {
                MySQLThruDataSource dataSource =
                        new MySQLThruDataSource(mip, mport, username, passwd, dbname);

                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                if (stmt.execute(checkSql)){
                    logger.info("check ok!");
                }else {
                    logger.error("check failed!");
                }
                conn.close();
            }catch (Exception e){
                e.printStackTrace();
                logger.error("check failed!");
            }
        }

        return map;
    }

    public Map pingRedis(){
        Map map = new HashMap();


        return map;
    }
}
