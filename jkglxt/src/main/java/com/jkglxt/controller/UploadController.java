package com.jkglxt.controller;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>();
        
        if (file.isEmpty()) {
            res.put("flag", 500);
            res.put("message", "请选择文件");
            return JSON.toJSONString(res);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extName;
        
        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        try {
            file.transferTo(new File(uploadDir + newFileName));
            String avatarUrl = "http://localhost:9000/uploads/avatars/" + newFileName;
            res.put("flag", 200);
            res.put("message", "上传成功");
            res.put("avatarUrl", avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
            res.put("flag", 500);
            res.put("message", "上传失败");
        }
        
        return JSON.toJSONString(res);
    }
}
