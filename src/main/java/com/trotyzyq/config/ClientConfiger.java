package com.trotyzyq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 客户端配置
 * @author zyq
 */
@Component
@ConfigurationProperties(prefix="oss")
public class ClientConfiger {

    /** 服务器地址**/
    private String ossServerUrl;

    /** 删除的服务器地址**/
    private String deleteServerUrl;

    /** token**/
    private String token;

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
}

