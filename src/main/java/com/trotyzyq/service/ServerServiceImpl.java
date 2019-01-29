package com.trotyzyq.service;

import com.trotyzyq.config.OssServerConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import com.trotyzyq.util.TimeUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 保存文件service
 * @author zyq
 */
@Service
public class ServerServiceImpl implements  IServerService {
    /** 配置类**/
    @Autowired
    private OssServerConfiger ossServerConfiger;
    /** 日志记录**/
    private Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);


    /**
     * 保存文件
     * @param fileList 文件列表
     * @return 成功true,失败false;
     */
    @Override
    public List<String> saveFile(List<FileDataEntity> fileList, String uploadPath) {
        boolean uploadSuccess = false;
        /** 保存成功的列表**/
        List<String> saveList = new ArrayList<>();
        String path = "";
        try {
            for(int i = 0; i < fileList.size() ;i++) {
                FileDataEntity fileDataEntity = fileList.get(i);
                String fileName = fileDataEntity.getFileName();
                String[] patten = fileName.split("\\.");
                String endPatten = patten[patten.length - 1];
                InputStream servletInputStream = null;
                String date = TimeUtil.getNowDate();
                String dicPath =  ossServerConfiger.getPathDirectory() + uploadPath + "/" + date + "/" ;
                File file = new File(dicPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                /** 生成随机数文件并写入到文件夹**/
                path = dicPath + TimeUtil.getCurrentTimeString().replaceAll("\\s", "")
                        .replaceAll("-", "").replaceAll("//", "")
                        .replaceAll(":", "") + UUID.randomUUID() + "." + endPatten;
                FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
                servletInputStream = fileDataEntity.getFileInputStream();
                IOUtils.copy(servletInputStream, fileOutputStream);

                /** 保存成功记录到文件记录文件**/
                File recordFile = new File(ossServerConfiger.getOssRecordPath());
                if (!recordFile.exists()) {
                    recordFile.createNewFile();
                }
                List<String> lines = IOUtils.readLines(new FileInputStream(recordFile));

                lines.add(path + "成功上传");
                OutputStream os = new FileOutputStream(recordFile);
                IOUtils.writeLines(lines, IOUtils.LINE_SEPARATOR, os);
                saveList.add(path);
                uploadSuccess = true;
            }
        }catch (IOException e) {
            logger.error(path + "上传失败");
            uploadSuccess = false;
        }

        /** 如果保存失败，就将之前保存成功的给删除**/
        if(!uploadSuccess){
            for(int i = 0 ;i < saveList.size();i++){
                String savePath = saveList.get(i);
                File deleteFile = new File(savePath);
                if(deleteFile.exists() && deleteFile.isFile()){
                     deleteFile.delete();
                }
            }
        }
        return saveList;
    }
}
