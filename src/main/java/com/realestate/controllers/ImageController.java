package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.dao.UserDetailsImpl;
import com.realestate.services.FTPService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private FTPService ftpService;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        return ftpService.uploadFile(file);
    }

    @GetMapping("/test")
    public String getMethodName(@RequestParam("name") String name) {    
        return "Hello" + name ;
    }
    
}
