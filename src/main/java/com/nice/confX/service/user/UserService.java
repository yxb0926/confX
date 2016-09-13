package com.nice.confX.service.user;

import org.springframework.stereotype.Service;

/**
 * Created by yxb on 16/9/13.
 */
@Service
public interface UserService {
    public Object login(String username, String password);

    public Object logout(String username, String password);

}
