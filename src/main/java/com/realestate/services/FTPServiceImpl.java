package com.realestate.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FTPServiceImpl implements FTPService{
    @Autowired
    private FTPClient ftpClient;

    @Value("${ftp.base-directory}")
    private String baseDirectory;

    @Override
    public String uploadFile(MultipartFile file) {
        try{
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            ftpClient.changeWorkingDirectory(baseDirectory);
            boolean success = ftpClient.storeFile(fileName, file.getInputStream());
            if (!success) {
                throw new RuntimeException("Failed to upload file to FTP server");
            }
            return fileName;
        }catch(IOException e){
            throw new RuntimeException("Failed to upload file to FTP server", e);
        }        
    }

    @Override
    public InputStream downloadFile(String fileName) {
        try {
            ftpClient.changeWorkingDirectory(baseDirectory);
            return ftpClient.retrieveFileStream(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file from FTP server", e);
        }
    }

}
