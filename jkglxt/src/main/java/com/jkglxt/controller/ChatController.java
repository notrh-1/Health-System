package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.ChatMessage;
import com.jkglxt.bean.UserProfile;
import com.jkglxt.mapper.ChatMessageMapper;
import com.jkglxt.mapper.UserProfileMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @GetMapping("/list")
    public String getList() {
        List<ChatMessage> list = chatMessageMapper.getList();
        for (ChatMessage message : list) {
            UserProfile profile = userProfileMapper.getByUserId(message.getUserId());
            if (profile != null) {
                message.setAvatar(profile.getAvatar());
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    public String add(@RequestBody ChatMessage message, HttpServletRequest request) {
        String userId = request.getHeader("userId");
        String username = request.getHeader("username");
        
        if (userId == null || userId.isEmpty()) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 500);
            res.put("message", "请先登录");
            return JSON.toJSONString(res);
        }
        
        message.setUserId(Integer.parseInt(userId));
        message.setUsername(username);
        
        int result = chatMessageMapper.add(message);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "发布成功");
        } else {
            res.put("flag", 500);
            res.put("message", "发布失败");
        }
        return JSON.toJSONString(res);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能删除消息");
            return JSON.toJSONString(res);
        }
        
        int result = chatMessageMapper.delete(id);
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
}
