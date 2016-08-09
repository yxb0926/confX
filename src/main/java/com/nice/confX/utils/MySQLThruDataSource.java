package com.nice.confX.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by yxb on 16/8/9.
 */
public class MySQLThruDataSource {
    private String ip;
    private String port;
    private String username;
    private String passwd;
    private String dbname;
    private String url;

    public MySQLThruDataSource(String ip, String port, String username, String passwd, String dbname){
        this.ip       = ip;
        this.port     = port;
        this.username = username;
        this.passwd   = passwd;
        this.dbname   = dbname;

        this.url = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
    }

    public Connection getConnection() throws SQLException{
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(this.url);
        dataSource.setUser(this.username);
        dataSource.setPassword(this.passwd);

        return dataSource.getConnection();
    }
}
