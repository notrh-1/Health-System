package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.CalorieRecord;
import com.jkglxt.bean.MealPlan;
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
@RequestMapping("/meal")
@CrossOrigin
public class MealPlanController {

    @Autowired
    private MealPlanMapper mealPlanMapper;

    @Autowired
    private CalorieRecordMapper calorieRecordMapper;

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
        
        List<MealPlan> list = mealPlanMapper.getByUserId(userId);
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
        
        List<MealPlan> list = mealPlanMapper.getByUserIdAndDate(userId, date);
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
        
        Map<String, Object> stats = mealPlanMapper.getNutritionStats(userId, date);
        Map<String, Object> res = new HashMap<>();
        res.put("stats", stats);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }

    @PostMapping("/add")
    @Transactional
    public String add(@RequestBody MealPlan mealPlan, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        if (!"admin".equals(userRole) && !String.valueOf(mealPlan.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能添加自己的数据");
            return JSON.toJSONString(res);
        }
        
        Map<String, Object> res = new HashMap<>();
        try {
            int result = mealPlanMapper.add(mealPlan);
            if (result > 0) {
                CalorieRecord calorieRecord = new CalorieRecord();
                calorieRecord.setUserId(mealPlan.getUserId());
                calorieRecord.setType("摄入");
                calorieRecord.setName(mealPlan.getFoodName());
                calorieRecord.setCalories(mealPlan.getCalories());
                calorieRecord.setRecordDate(mealPlan.getMealDate());
                calorieRecord.setMealPlanId(mealPlan.getId());
                
                int calorieResult = calorieRecordMapper.add(calorieRecord);
                if (calorieResult > 0) {
                    mealPlanMapper.updateCalorieRecordId(mealPlan.getId(), calorieRecord.getId());
                }
                
                res.put("flag", 200);
                res.put("message", "添加成功，已同步到卡路里");
            } else {
                res.put("flag", 500);
                res.put("message", "添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        
        MealPlan mealPlan = mealPlanMapper.getById(id);
        if (mealPlan != null && !"admin".equals(userRole) && !String.valueOf(mealPlan.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能删除自己的数据");
            return JSON.toJSONString(res);
        }
        
        Map<String, Object> res = new HashMap<>();
        try {
            if (mealPlan != null && mealPlan.getCalorieRecordId() != null) {
                calorieRecordMapper.delete(mealPlan.getCalorieRecordId());
            }
            int result = mealPlanMapper.delete(id);
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

    @PutMapping("/updateCalorieRecordId/{mealPlanId}/{calorieRecordId}")
    public String updateCalorieRecordId(@PathVariable Integer mealPlanId, @PathVariable Integer calorieRecordId, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");
        
        MealPlan mealPlan = mealPlanMapper.getById(mealPlanId);
        if (mealPlan != null && !"admin".equals(userRole) && !String.valueOf(mealPlan.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能操作自己的数据");
            return JSON.toJSONString(res);
        }
        
        int result = mealPlanMapper.updateCalorieRecordId(mealPlanId, calorieRecordId);
        Map<String, Object> res = new HashMap<>();
        if (result > 0) {
            res.put("flag", 200);
            res.put("message", "更新成功");
        } else {
            res.put("flag", 500);
            res.put("message", "更新失败");
        }
        return JSON.toJSONString(res);
    }
}
