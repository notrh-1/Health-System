package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.User;
import com.jkglxt.mapper.UserMapper;
import com.jkglxt.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public String getUserList() {
        List<User> users = userMapper.getUserList();
        Map<String, Object> res = new HashMap<>();
        res.put("users", users);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    public String addUser(@RequestBody User user, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能添加用户");
            return JSON.toJSONString(res);
        }
        int result = userMapper.addUser(user);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "添加成功");
        } else {
            res.put("flag", 500);
            res.put("message", "添加失败");
        }
        return JSON.toJSONString(res);
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody User user, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能修改用户");
            return JSON.toJSONString(res);
        }
        int result = userMapper.updateUser(user);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "修改成功");
        } else {
            res.put("flag", 500);
            res.put("message", "修改失败");
        }
        return JSON.toJSONString(res);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能删除用户");
            return JSON.toJSONString(res);
        }
        int result = userMapper.deleteUser(id);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "删除成功");
        } else {
            res.put("flag", 500);
            res.put("message", "删除失败");
        }
        return JSON.toJSONString(res);
    }

    @GetMapping("/get/{id}")
    public String getUserById(@PathVariable Integer id) {
        User user = userMapper.getUserById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("user", user);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/updateUsername")
    public String updateUsername(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String headerUserId = request.getHeader("userId");
        Integer id = Integer.valueOf(params.get("id").toString());
        String username = params.get("username") != null ? params.get("username").toString() : "";
        
        if (!String.valueOf(id).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能修改自己的用户名");
            return JSON.toJSONString(res);
        }
        
        if (username == null || username.trim().isEmpty()) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 400);
            res.put("message", "用户名不能为空");
            return JSON.toJSONString(res);
        }
        
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        
        int result = userMapper.updateUser(user);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "用户名修改成功");
        } else {
            res.put("flag", 500);
            res.put("message", "用户名修改失败");
        }
        return JSON.toJSONString(res);
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String headerUserId = request.getHeader("userId");
        Integer id = Integer.valueOf(params.get("id").toString());
        String oldPassword = params.get("oldPassword") != null ? params.get("oldPassword").toString() : "";
        String newPassword = params.get("newPassword") != null ? params.get("newPassword").toString() : "";
        
        if (!String.valueOf(id).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能修改自己的密码");
            return JSON.toJSONString(res);
        }
        
        User user = userMapper.getUserById(id);
        if (user == null) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 404);
            res.put("message", "用户不存在");
            return JSON.toJSONString(res);
        }
        
        if (!MD5Util.encrypt(oldPassword).equals(user.getPassword())) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 400);
            res.put("message", "原密码不正确");
            return JSON.toJSONString(res);
        }
        
        if (newPassword == null || newPassword.trim().length() < 6) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 400);
            res.put("message", "新密码至少6位");
            return JSON.toJSONString(res);
        }
        
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.encrypt(newPassword));
        
        int result = userMapper.updateUser(updateUser);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "密码修改成功");
        } else {
            res.put("flag", 500);
            res.put("message", "密码修改失败");
        }
        return JSON.toJSONString(res);
    }
}
