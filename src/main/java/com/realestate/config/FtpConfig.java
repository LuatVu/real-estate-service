package com.realestate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
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

    @Value("${ftp.use-tls}")
    private boolean useTls;

    private FTPSClient  ftpClient;

    @Bean
    public FTPSClient ftpClient() {
        ftpClient = new FTPSClient ("TLS");
        ftpClient.setUseClientMode(true);
        try {
            ftpClient.connect(ftpServer, ftpPort);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.enterLocalPassiveMode();            
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            if (useTls) {
                ftpClient.execPBSZ(0); 
                ftpClient.execPROT("P");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to FTP server", e);
        }
        return ftpClient;
    }

    @PreDestroy
    public void disconnect() {
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
