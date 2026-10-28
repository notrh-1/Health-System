package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.CalorieRecord;
import com.jkglxt.mapper.CalorieRecordMapper;
import com.jkglxt.mapper.MealPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calorie")
@CrossOrigin
public class CalorieRecordController {

    @Autowired
    private CalorieRecordMapper recordMapper;

    @Autowired
    private MealPlanMapper mealPlanMapper;

    @GetMapping("/list/{userId}")
    public String getList(@PathVariable Integer userId, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(userId).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能查看自己的数据");
            return JSON.toJSONString(res);
        }
        
        List<CalorieRecord> list = recordMapper.getByUserId(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @GetMapping("/list/{userId}/{date}")
    public String getListByDate(@PathVariable Integer userId, @PathVariable String date, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(userId).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能查看自己的数据");
            return JSON.toJSONString(res);
        }
        
        List<CalorieRecord> list = recordMapper.getByUserIdAndDate(userId, date);
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @GetMapping("/stats/{userId}/{date}")
    public String getStats(@PathVariable Integer userId, @PathVariable String date, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(userId).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能查看自己的数据");
            return JSON.toJSONString(res);
        }
        
        List<Map<String, Object>> stats = recordMapper.getStatsByUserIdAndDate(userId, date);
        Map<String, Object> res = new HashMap<>();
        res.put("stats", stats);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    public String add(@RequestBody CalorieRecord record, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(record.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能添加自己的数据");
            return JSON.toJSONString(res);
        }
        
        int result = recordMapper.add(record);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("data", record.getId());
            res.put("message", "添加成功");
        } else {
            res.put("flag", 500);
            res.put("message", "添加失败");
        }
        return JSON.toJSONString(res);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        CalorieRecord record = recordMapper.getById(id);
        if (record != null && !"admin".equals(userRole) && !String.valueOf(record.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能删除自己的数据");
            return JSON.toJSONString(res);
        }
        
        Map<String, Object> res = new HashMap<>();
        try {
            if (record != null && record.getMealPlanId() != null) {
                mealPlanMapper.delete(record.getMealPlanId());
            }
            int result = recordMapper.delete(id);
            if (result > 0) {
                res.put("flag", 200);
                res.put("message", "删除成功");
            } else {
                res.put("flag", 500);
                res.put("message", "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.put("flag", 500);
            res.put("message", "删除失败");
        }
        return JSON.toJSONString(res);
    }
}
