package com.realestate.config;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class FTPClientCustom extends FTPClient{
    private String ftpServer;
    private int ftpPort;
    private String ftpUsername;
    private String ftpPassword;


    public void reconnect() throws SocketException, IOException{
        try{
            if(!this.isConnected() || !this.sendNoOp()){
                this.connect(ftpServer, ftpPort);        
                this.login(ftpUsername, ftpPassword);           
                this.enterLocalPassiveMode();            
                this.setFileType(FTPClient.BINARY_FILE_TYPE);
                log.info("Reconnected FTP server.");
            }
        }catch(SocketException se){            
            this.connect(ftpServer, ftpPort);        
            this.login(ftpUsername, ftpPassword);           
            this.enterLocalPassiveMode();            
            this.setFileType(FTPClient.BINARY_FILE_TYPE);
            log.info("Reconnected FTP server.");
        }
        catch(IOException e){            
            e.printStackTrace();
        }
    }
}
