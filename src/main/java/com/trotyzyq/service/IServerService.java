package com.trotyzyq.service;

import com.trotyzyq.entity.bo.FileDataEntity;

import java.util.List;

/**
 * 保存文件service
 * @author zyq
 */
public interface IServerService {

    /**
     * 保存文件
     * @param fileList 文件列表
     * @param uploadSubPath 子路径
     * @return 成功的列表集合
     */
    List<String> saveFile(List<FileDataEntity> fileList, String uploadSubPath);
}
