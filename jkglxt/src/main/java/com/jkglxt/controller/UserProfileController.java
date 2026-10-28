package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.UserProfile;
import com.jkglxt.mapper.UserProfileMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class UserProfileController {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @GetMapping("/get/{userId}")
    public String getByUserId(@PathVariable Integer userId, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(userId).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能查看自己的信息");
            return JSON.toJSONString(res);
        }
        
        UserProfile profile = userProfileMapper.getByUserId(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("profile", profile);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/save")
    public String save(@RequestBody UserProfile profile, HttpServletRequest request) {
        String userId = request.getHeader("userId");
        
        if (userId == null || userId.isEmpty()) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 500);
            res.put("message", "请先登录");
            return JSON.toJSONString(res);
        }
        
        profile.setUserId(Integer.parseInt(userId));
        
        UserProfile existProfile = userProfileMapper.getByUserId(Integer.parseInt(userId));
        
        int result;
        if (existProfile == null) {
            result = userProfileMapper.add(profile);
        } else {
            result = userProfileMapper.update(profile);
        }
        
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "保存成功");
        } else {
            res.put("flag", 500);
            res.put("message", "保存失败");
        }
        return JSON.toJSONString(res);
    }
}
