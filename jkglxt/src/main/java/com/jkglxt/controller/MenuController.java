package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import com.jkglxt.bean.MainMenu;
import com.jkglxt.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
@CrossOrigin
public class MenuController {

    @Autowired
    private MenuMapper menuMapper;

    @RequestMapping("/getMenus")
    public String getMenus() {
        List<MainMenu> menus = menuMapper.getMenus();
        Map<String, Object> res = new HashMap<>();
        res.put("menus", menus);
        res.put("flag", 200);
        return JSON.toJSONString(res);
    }
}
