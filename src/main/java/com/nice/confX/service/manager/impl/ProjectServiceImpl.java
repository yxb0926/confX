package com.nice.confX.service.manager.impl;

import com.nice.confX.service.manager.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/7/1.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
    *  增加项目情况
     *  返回值: 1 => success, 0 => failed
    * */
    @Override
    public Integer addProject(String pcode,  String pname,
                              String pdesc,
                              String ptype,  String pfilename){
        try {
            jdbcTemplate.update("INSERT INTO project_info(pcode, pname, pdesc, ptype, pfilename) " +
                    "VALUE (?,?,?,?,?)", pcode, pname, pdesc, ptype, pfilename);
            return 1;
        }catch (DataAccessException dataAccessException){
            return 0;
        }
    }

    /**
     *  删除项目
     * */
    @Override
    @Transactional
    public void delProject(String programName, String projectName, String type) throws Exception{
        String sql_1 = "DELETE FROM project_info WHERE pname=? AND pcode=? AND ptype=?";
        jdbcTemplate.update(sql_1, programName, projectName, type);

        String sql_2 = "DELETE FROM appname_info WHERE appname=? AND pname=? AND type=?";
        jdbcTemplate.update(sql_2, projectName, programName, type);

        String sql_3 = "DELETE FROM config_info WHERE program_id=? AND data_id=?";
        jdbcTemplate.update(sql_3, programName, projectName);

        String sql_4 = "";
        if (type.equals("MySQL")){
            sql_4 = "DELETE FROM groupname_info_mysql WHERE appname=? AND pname=?";
        } else if(type.equals("Redis")){
            sql_4 = "DELETE FROM groupname_info_redis WHERE appname=? AND pname=?";
        }

        jdbcTemplate.update(sql_4, projectName, programName);
    }

    /**
     *  查询项目信息
     * */
    @Override
    public List<String> queryProject(){
        List<String>  pList = new ArrayList<String>();
        return pList;
    }

    /**
     *  查询全部项目信息
     * */
    @Override
    public List queryAllProject(){
        List pAllList = new ArrayList();
        try{
            pAllList = jdbcTemplate.queryForList("SELECT * FROM project_info");
        }catch (Exception e){
            e.printStackTrace();
        }
        return pAllList;
    }

    @Override
    public List queryProject(String pname) {
        String sql = "SELECT * FROM project_info WHERE pname=?";
        List list = jdbcTemplate.queryForList(sql, pname);

        return list;
    }

    @Override
    @Transactional
    public List getProject(HttpServletRequest request) {
        String pname  = request.getParameter("pname");
        String ip     = request.getParameter("ip");
        String dbname = request.getParameter("dbname");
        String type   = request.getParameter("type");

        List list = new ArrayList();
        String sql_mysqlgroup = "SELECT pname, appname FROM groupname_info_mysql WHERE 1 = 1";
        String sql_redisgroup = "SELECT pname, appname FROM groupname_info_redis WHERE 1 = 1";
        String sql_group = null;
        if (type == null) {
            list = this.queryAllProject();
            return list;
        } else if (type.equalsIgnoreCase("ALL")) {
            if (pname != null && pname.trim().length() > 0) {
                sql_mysqlgroup += " AND pname='" + pname.trim() + "'";
                sql_redisgroup += " AND pname='" + pname.trim() + "'";
            }

            if (ip != null && ip.trim().length() > 0) {
                sql_mysqlgroup += " AND ip='" + ip.trim() + "'";
                sql_redisgroup += " AND ip='" + ip.trim() + "'";
            }

            if (dbname != null && dbname.trim().length() > 0) {
                sql_mysqlgroup += " AND dbname='" + dbname.trim() + "'";
            }
            sql_group = sql_mysqlgroup + " union " + sql_redisgroup;
        } else if (type.equalsIgnoreCase("MySQL")) {
            if (pname != null && pname.trim().length() > 0) {
                sql_mysqlgroup += " AND pname='" + pname.trim() + "'";
            }
            if (ip != null && ip.trim().length() > 0) {
                sql_mysqlgroup += " AND ip='" + ip.trim() + "'";
            }
            if (dbname != null && dbname.trim().length() > 0) {
                sql_mysqlgroup += " AND dbname='" + dbname.trim() + "'";
            }
            sql_group = sql_mysqlgroup;
        } else if (type.equalsIgnoreCase("Redis")) {
            if (pname != null && pname.trim().length() > 0) {
                sql_redisgroup += " AND pname='" + pname.trim() + "'";
            }
            if (ip != null && ip.trim().length() > 0) {
                sql_redisgroup += " AND ip='" + ip.trim() + "'";
            }
            sql_group = sql_redisgroup;
        }

        List<Map<String, Object>> list_group = jdbcTemplate.queryForList(sql_group);

        String sql = "SELECT * FROM project_info WHERE 1=1 ";

        String sqlstr_pname   = "";
        String sqlstr_appname = "";
        String where_pname    = "";
        String where_appname  = "";
        if (list_group.size() == 0 ){
           return list;
        }else{
            for (int i=0; i<list_group.size(); i++){
                String pgramname = list_group.get(i).get("pname").toString();
                String appname   = list_group.get(i).get("appname").toString();

                sqlstr_pname += "'" + pgramname + "',";
                sqlstr_appname += "'" + appname + "',";
            }
        }

        where_pname = sqlstr_pname.substring(0, sqlstr_pname.length()-1);
        where_appname = sqlstr_appname.substring(0,sqlstr_appname.length()-1);

        String projectSql = sql + " AND pname in(" + where_pname + ")" + " AND pcode in(" + where_appname + ")";

        list = jdbcTemplate.queryForList(projectSql);

        return list;
    }


    @Override
    public List queryProjectByIpDBnameType(String ip, String dbname, String type) {
        return null;
    }

    @Override
    public Boolean checkExist(String dataid, String groupid) {
        try{
            String sql = "SELECT COUNT(1) AS cnt FROM config_info WHERE data_id=? AND group_id=? ";
            List<Map<String, Object>> rs = jdbcTemplate.queryForList(sql, dataid, groupid);

            Number number = (Number) rs.get(0).get("cnt");
            int cnt = number.intValue();

            if ( cnt > 0 ){
                return true;
            }else {
                return false;
            }

        }catch (DataAccessException e){
            e.printStackTrace();
            return true;
        }
    }

    @Override
    @Transactional
    public Integer addProgram(HttpServletRequest request) {
        String pname     = request.getParameter("pname");
        String pdesc     = request.getParameter("pdesc");
        String prange    = request.getParameter("prange");
        String pstatus   = request.getParameter("pstatus");
        String powner    = request.getParameter("powner");
        String ppath     = request.getParameter("ppath");
        String pcmd      = request.getParameter("pcmd");
        String psysuser  = request.getParameter("psysuser");
        String pcodetype = request.getParameter("pcodetype");

        java.util.Date date = new java.util.Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gmt_created = simpleDateFormat.format(date);

        int rt = 1;

        try {
            String sql = "REPLACE INTO project_project " +
                    "(pname, pstatus, pdesc, prange, owner, path, pcmd, psysuser, pcodetype, gmt_created) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?)";
            int rs = jdbcTemplate.update(sql, pname, pstatus, pdesc, prange, powner, ppath, pcmd, psysuser, pcodetype, gmt_created);

            if (rs>0){
                rt = 1;
            }

        }catch (DataAccessException e){
            e.printStackTrace();
            rt = 0;
        }

        return rt;
    }

    @Override
    public List queryAllProgram() {
        List pList = new ArrayList();
        try {
            String sql = "SELECT pname, pstatus, pdesc, prange, owner, path, pcmd, psysuser, pcodetype, gmt_created " +
                    "FROM project_project ORDER BY pstatus DESC";
            pList = jdbcTemplate.queryForList(sql);
        }catch (Exception e){
            e.printStackTrace();
        }

        return pList;
    }

    @Override
    @Transactional
    public List queryProgramByIport(String iport) {
        List list = new ArrayList();
        try {
            //select pname from groupname_info_mysql where ip like '%10.10.10.81%'
            // union select pname from groupname_info_redis where ip like '%10.10.10.81%';
            String sql  = "SELECT pname FROM groupname_info_mysql " +
                    "WHERE ip LIKE ? OR pname LIKE ? OR appname LIKE ? OR groupname LIKE ? OR dbname LIKE ? " +
                    "UNION SELECT pname FROM groupname_info_redis " +
                    "WHERE ip LIKE ? OR pname LIKE ? OR appname LIKE ? OR groupname LIKE ? ";
            List<Map<String, Object>> list1 =
                    jdbcTemplate.queryForList(sql, "%"+iport+"%", "%"+iport+"%", "%"+iport+"%", "%"+iport+"%",
                            "%"+iport+"%", "%"+iport+"%", "%"+iport+"%", "%"+iport+"%", "%"+iport+"%");

            String tmpStr = "";
            for (int i=0; i<list1.size(); i++){
                tmpStr += "'"+list1.get(i).get("pname")+"'";
                tmpStr += ",";
            }
            String whereStr;
            if( tmpStr.length()>=1){
                whereStr = tmpStr.substring(0,tmpStr.length()-1);
            }else {
                whereStr = "''";
            }

            String sql1 = "SELECT pname, pstatus, pdesc, prange, " +
                    "owner, path, pcmd, psysuser, pcodetype, gmt_created " +
                    "FROM project_project WHERE pname in "+ "("+ whereStr + ")"
                    +" ORDER BY pstatus DESC";

            list = jdbcTemplate.queryForList(sql1);

        }catch (SQLWarningException e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List queryProgram(String pname) {
        String sql = "SELECT * FROM project_project WHERE pname=?";
        List list = jdbcTemplate.queryForList(sql, pname);
        return list;
    }

    @Override
    @Transactional
    public void delProgram(String pname) throws Exception {
        String sql_1 = "DELETE FROM project_project WHERE pname=?";
        jdbcTemplate.update(sql_1, pname);

        String sql_2 = "DELETE FROM project_info WHERE pname=?";
        jdbcTemplate.update(sql_2, pname);

        String sql_3 = "DELETE FROM groupname_info_mysql WHERE pname=?";
        jdbcTemplate.update(sql_3, pname);

        String sql_4 = "DELETE FROM groupname_info_redis WHERE pname=?";
        jdbcTemplate.update(sql_4, pname);

        String sql_5 = "DELETE FROM config_info WHERE program_id=?";
        jdbcTemplate.update(sql_5, pname);

        String sql_6 = "DELETE FROM client_list WHERE pname=?";
        jdbcTemplate.update(sql_6, pname);

        String sql_7 = "DELETE FROM appname_info WHERE pname=?";
        jdbcTemplate.update(sql_7, pname);

    }

}
