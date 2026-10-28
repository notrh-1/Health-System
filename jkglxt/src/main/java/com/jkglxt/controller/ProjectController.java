package com.jkglxt.controller;

import com.jkglxt.bean.Project;
import com.jkglxt.mapper.ProjectMapper;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectMapper projectMapper;

    @PostMapping("/add")
    public String addProject(@RequestBody Project project, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");

        if (!"admin".equals(userRole) && !String.valueOf(project.getUserId()).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能添加自己的运动项目");
            return JSON.toJSONString(res);
        }

        int result = projectMapper.addProject(project);
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

    @DeleteMapping("/delete/{id}")
    public String deleteProject(@PathVariable Integer id, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");

        // 先查询项目信息，检查权限
        List<Project> projects = projectMapper.getByUserId(Integer.parseInt(headerUserId));
        boolean hasPermission = false;
        if ("admin".equals(userRole)) {
            hasPermission = true;
        } else {
            for (Project project : projects) {
                if (project.getId().equals(id)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能删除自己的运动项目");
            return JSON.toJSONString(res);
        }

        int result = projectMapper.deleteProject(id);
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

    @GetMapping("/list/{userId}")
    public String getProjectList(@PathVariable Integer userId, HttpServletRequest request) {
        String userRole = request.getHeader("userRole");
        String headerUserId = request.getHeader("userId");

        if (!"admin".equals(userRole) && !String.valueOf(userId).equals(headerUserId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("flag", 403);
            res.put("message", "只能查看自己的运动项目");
            return JSON.toJSONString(res);
        }

        List<Project> list = projectMapper.getByUserId(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("list", list);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }
}