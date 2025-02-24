package com.realestate.services;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface FTPService {
    String uploadFile(MultipartFile file);
    InputStream downloadFile(String fileName);
}
