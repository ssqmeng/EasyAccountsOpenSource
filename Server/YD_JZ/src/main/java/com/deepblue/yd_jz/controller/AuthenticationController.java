package com.deepblue.yd_jz.controller;

// AuthenticationController.java
import com.deepblue.yd_jz.dto.BaseDto;
import com.deepblue.yd_jz.dto.LoginRequestDto;
import com.deepblue.yd_jz.exception.UnauthorizedException;
import com.deepblue.yd_jz.service.UserService;
import com.deepblue.yd_jz.utils.JwtTokenUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;  // 用户认证服务（假设你已经有了该服务）

    @PostMapping("/login")
    public BaseDto login(@RequestBody LoginRequestDto loginRequest) {
        // 用户认证逻辑（实际开发中可以查询数据库进行验证）
        if (userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            // 生成 JWT Token
           String token= JwtTokenUtil.generateToken(loginRequest.getUsername());
            BaseDto baseDto = BaseDto.setSuccessBean();
            baseDto.setData(token);
            return baseDto;
        } else {
            BaseDto baseDto = BaseDto.setErrorBean("Invalid username or password",500);
//            throw new UnauthorizedException("Invalid username or password");
            return baseDto;
        }

    }
}

