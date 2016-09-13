package com.nice.confX.service.user.impl;

import com.nice.confX.model.User;
import com.nice.confX.service.user.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yxb on 16/9/13.
 */
@Service
public class UserServiceImpl implements UserService{

    private Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object login(String username, String password) {
        String passwordMd5 = DigestUtils.md5Hex(password);
        String sql = "SELECT * FROM userinfo WHERE username=? AND password=?";
        try{
            List list = jdbcTemplate.queryForList(sql, username, passwordMd5);

            if(list.size()==1){
                logger.debug("登陆成功!");
                User user = new User();
                user.setUsername(((Map) list.get(0)).get("username").toString());
                user.setPassword(((Map) list.get(0)).get("password").toString());
                user.setRole(((Map) list.get(0)).get("role").toString());
                user.setComment(((Map) list.get(0)).get("comment").toString());

                return user;
            } else {
                // 用户名或密码错误
                logger.debug("登陆失败,用户名或密码错误!");
                return "登陆失败,用户名或密码错误!";
            }

        }catch (Exception e){
            e.printStackTrace();
            return "登陆失败!";
        }
    }

    @Override
    public Object logout(String username, String password) {
        return null;
    }
}
