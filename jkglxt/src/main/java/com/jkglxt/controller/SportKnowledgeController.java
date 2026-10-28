package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.SportKnowledge;
import com.jkglxt.mapper.SportKnowledgeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/knowledge")
@CrossOrigin
public class SportKnowledgeController {

    @Autowired
    private SportKnowledgeMapper knowledgeMapper;

    @GetMapping("/list")
    public String getList() {
        List<SportKnowledge> list = knowledgeMapper.getList();
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @GetMapping("/get/{id}")
    public String getById(@PathVariable Integer id) {
        SportKnowledge knowledge = knowledgeMapper.getById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("data", knowledge);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @GetMapping("/category/{category}")
    public String getByCategory(@PathVariable String category) {
        List<SportKnowledge> list = knowledgeMapper.getByCategory(category);
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    public String add(@RequestBody SportKnowledge knowledge, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能添加科普文章");
            return JSON.toJSONString(res);
        }
        
        int result = knowledgeMapper.add(knowledge);
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
    public String update(@RequestBody SportKnowledge knowledge, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能修改科普文章");
            return JSON.toJSONString(res);
        }
        
        int result = knowledgeMapper.update(knowledge);
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
    public String delete(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能删除科普文章");
            return JSON.toJSONString(res);
        }
        
        int result = knowledgeMapper.delete(id);
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
