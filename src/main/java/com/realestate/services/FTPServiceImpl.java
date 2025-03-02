package com.realestate.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ", "_");
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
    public InputStreamResource downloadFile(String fileName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            ftpClient.changeWorkingDirectory(baseDirectory);
            InputStream inputStream = ftpClient.retrieveFileStream(fileName);
            if (inputStream == null) {
                throw new IOException("File not found or could not be retrieved.");
            }
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            ftpClient.completePendingCommand();
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

            return resource;
        } catch (Exception e) {
            throw new IOException("Failed to download file from FTP server", e);
        }finally{
            outputStream.close();
        }
    }

}
