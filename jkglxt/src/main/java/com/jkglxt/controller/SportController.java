package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.Sport;
import com.jkglxt.mapper.SportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sport")
@CrossOrigin
public class SportController {

    @Autowired
    private SportMapper sportMapper;

    @GetMapping("/list")
    public String getSportList() {
        List<Sport> sports = sportMapper.getSportList();
        Map<String, Object> res = new HashMap<>();
        res.put("sports", sports);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @GetMapping("/get/{id}")
    public String getSportById(@PathVariable Integer id) {
        Sport sport = sportMapper.getSportById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("sport", sport);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    public String addSport(@RequestBody Sport sport, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能添加运动项目");
            return JSON.toJSONString(res);
        }
        
        int result = sportMapper.addSport(sport);
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
    public String updateSport(@RequestBody Sport sport, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能修改运动项目");
            return JSON.toJSONString(res);
        }
        
        int result = sportMapper.updateSport(sport);
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
    public String deleteSport(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        if (!"admin".equals(userRole)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只有管理员才能删除运动项目");
            return JSON.toJSONString(res);
        }
        
        int result = sportMapper.deleteSport(id);
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
