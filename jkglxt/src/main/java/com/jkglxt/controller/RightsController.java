package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.User;
import com.jkglxt.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rights")
@CrossOrigin
public class RightsController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public String getRightsList(HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能访问权限管理");
            return JSON.toJSONString(res);
        }
        
        List<User> users = userMapper.getUserList();
        Map<String, Object> res = new HashMap<>();
        res.put("users", users);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PutMapping("/updateRole/{id}")
    public String updateRole(@PathVariable Integer id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能修改角色");
            return JSON.toJSONString(res);
        }
        
        String role = params.get("role");
        int result = userMapper.updateRole(id, role);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "角色修改成功");
        } else {
            res.put("flag", 500);
            res.put("message", "角色修改失败");
        }
        return JSON.toJSONString(res);
    }

    @PutMapping("/updateState/{id}")
    public String updateState(@PathVariable Integer id, @RequestBody Map<String, Integer> params, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能修改状态");
            return JSON.toJSONString(res);
        }
        
        Integer state = params.get("state");
        int result = userMapper.updateState(id, state);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "状态修改成功");
        } else {
            res.put("flag", 500);
            res.put("message", "状态修改失败");
        }
        return JSON.toJSONString(res);
    }
}
