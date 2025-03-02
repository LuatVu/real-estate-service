package com.realestate.services;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FTPService {
    String uploadFile(MultipartFile file);
    InputStreamResource downloadFile(String fileName) throws IOException;
}
