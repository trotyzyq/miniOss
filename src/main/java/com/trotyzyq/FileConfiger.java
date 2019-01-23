package com.trotyzyq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置类
 * @author zyq
 */
@Component
@ConfigurationProperties(prefix="oss")
public class FileConfiger {

    /** 文件保存路径 **/
    private String pathDirectory;
    /** 获取文件地址 **/
    private String getFilePathUrl;
    /** 日志记录路径 **/
    private String ossRecordPath;

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
}
