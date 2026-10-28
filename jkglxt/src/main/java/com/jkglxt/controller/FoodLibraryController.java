package com.jkglxt.controller;

import com.jkglxt.bean.FoodLibrary;
import com.jkglxt.mapper.FoodLibraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/food")
@CrossOrigin
public class FoodLibraryController {

    @Autowired
    private FoodLibraryMapper foodLibraryMapper;

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<FoodLibrary> list = foodLibraryMapper.findAll();
            result.put("flag", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "查询失败");
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/types")
    public Map<String, Object> types() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> types = foodLibraryMapper.findAllTypes();
            result.put("flag", 200);
            result.put("data", types);
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "查询失败");
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            FoodLibrary food = foodLibraryMapper.findById(id);
            result.put("flag", 200);
            result.put("data", food);
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "查询失败");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody FoodLibrary foodLibrary) {
        Map<String, Object> result = new HashMap<>();
        try {
            foodLibraryMapper.insert(foodLibrary);
            result.put("flag", 200);
            result.put("data", foodLibrary);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "添加失败");
            e.printStackTrace();
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody FoodLibrary foodLibrary) {
        Map<String, Object> result = new HashMap<>();
        try {
            foodLibraryMapper.update(foodLibrary);
            result.put("flag", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "更新失败");
            e.printStackTrace();
        }
        return result;
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            foodLibraryMapper.deleteById(id);
            result.put("flag", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("flag", 500);
            result.put("message", "删除失败");
            e.printStackTrace();
        }
        return result;
    }
}
