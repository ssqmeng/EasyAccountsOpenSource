package com.deepblue.yd_jz.service;

// UserService.java
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 假设这里有一个简单的验证方法，实际中会验证用户名和密码
    public boolean authenticate(String username, String password) {
        // 示例：硬编码用户名和密码
        return "sugar".equals(username) && "hanhan".equals(password);
    }
}
