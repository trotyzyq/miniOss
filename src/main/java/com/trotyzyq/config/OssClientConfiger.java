package com.trotyzyq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 客户端配置
 * @author zyq
 */
@Component
@ConfigurationProperties(prefix="ossClient")
public class OssClientConfiger {

    /** 服务器地址**/
    private String ossServerUrl;

    /** 删除的服务器地址**/
    private String deleteServerUrl;

    /** token**/
    private String token;

    /** 客户端私钥**/
    private String clientPrivateKey;

    /** 客户端公钥**/
    private String clientPublicKey;

    /** 服务端公钥**/
    private String serverPublicKey;

    public String getOssServerUrl() {
        return ossServerUrl;
    }

    public void setOssServerUrl(String ossServerUrl) {
        this.ossServerUrl = ossServerUrl;
    }

    public String getDeleteServerUrl() {
        return deleteServerUrl;
    }

    public void setDeleteServerUrl(String deleteServerUrl) {
        this.deleteServerUrl = deleteServerUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientPrivateKey() {
        return clientPrivateKey;
    }

    public void setClientPrivateKey(String clientPrivateKey) {
        this.clientPrivateKey = clientPrivateKey;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(String serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }
}

