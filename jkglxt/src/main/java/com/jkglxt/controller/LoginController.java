package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.User;
import com.jkglxt.mapper.UserMapper;
import com.jkglxt.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        String flag = "error";
        User us = userMapper.login(user.getUsername(), user.getPassword());
        if (us == null) {
            us = userMapper.login(user.getUsername(), MD5Util.encrypt(user.getPassword()));
        }
        Map<String, Object> res = new HashMap<>();
        if (us != null) {
            if (us.getState() != null && us.getState() == 0) {
                res.put("flag", 403);
                res.put("message", "账号已被禁用，请联系管理员");
                return JSON.toJSONString(res);
            }
            flag = "ok";
        }
        res.put("flag", flag);
        res.put("user", us);
        return JSON.toJSONString(res);
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        Map<String, Object> res = new HashMap<>();
        
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            res.put("flag", 500);
            res.put("message", "用户名已存在");
            return JSON.toJSONString(res);
        }
        
        if (user.getRole() == null) {
            user.setRole("普通用户");
        }
        if (user.getState() == null) {
            user.setState(1);
        }
        
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        
        int result = userMapper.addUser(user);
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "注册成功");
        } else {
            res.put("flag", 500);
            res.put("message", "注册失败");
        }
        return JSON.toJSONString(res);
    }
}
