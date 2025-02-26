package com.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import jakarta.annotation.PreDestroy;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FtpConfig {
    @Value("${ftp.server}")
    private String ftpServer;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;   

    private FTPClient ftpClient;

    @Bean
    public FTPClient ftpClient() {
        ftpClient = new FTPClient();        
        try {
            ftpClient.connect(ftpServer, ftpPort);        
            ftpClient.login(ftpUsername, ftpPassword);           
            ftpClient.enterLocalPassiveMode();            
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);            
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to FTP server", e);
        }
        return ftpClient;
    }

    @PreDestroy
    public void disconnect(){
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
