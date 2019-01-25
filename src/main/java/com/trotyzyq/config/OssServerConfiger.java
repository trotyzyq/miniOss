package com.trotyzyq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务端配置类
 * @author zyq
 */
@Component
@ConfigurationProperties(prefix="ossServer")
public class OssServerConfiger {

    /** 文件保存路径 **/
    private String pathDirectory;

    /** 获取文件地址 **/
    private String getFilePathUrl;

    /** 日志记录路径 **/
    private String ossRecordPath;

    /** token**/
    private String token;

    /** 服务端私钥**/
    private String serverPrivateKey;

    /** 服务端公钥**/
    private String serverPublicKey;

    /** 客户端端公钥**/
    private String clientPublicKey;

    public String getPathDirectory() {
        return pathDirectory;
    }

    public void setPathDirectory(String pathDirectory) {
        this.pathDirectory = pathDirectory;
    }

    public String getGetFilePathUrl() {
        return getFilePathUrl;
    }

    public void setGetFilePathUrl(String getFilePathUrl) {
        this.getFilePathUrl = getFilePathUrl;
    }

    public String getOssRecordPath() {
        return ossRecordPath;
    }

    public void setOssRecordPath(String ossRecordPath) {
        this.ossRecordPath = ossRecordPath;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getServerPrivateKey() {
        return serverPrivateKey;
    }

    public void setServerPrivateKey(String serverPrivateKey) {
        this.serverPrivateKey = serverPrivateKey;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }
}
