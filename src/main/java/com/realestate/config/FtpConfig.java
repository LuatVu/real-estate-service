package com.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@Slf4j
public class FtpConfig {
    @Value("${ftp.server}")
    private String ftpServer;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;   

    private FTPClientCustom ftpClient;

    @Bean
    FTPClientCustom ftpClient() {
        ftpClient = new FTPClientCustom(ftpServer, ftpPort, ftpUsername, ftpPassword);
        try {
            ftpClient.connect(ftpServer, ftpPort);        
            ftpClient.login(ftpUsername, ftpPassword);           
            ftpClient.enterLocalPassiveMode();            
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);            
            log.info("Connected FTP server!!!");
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
                log.info("Disconnected FTP server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
