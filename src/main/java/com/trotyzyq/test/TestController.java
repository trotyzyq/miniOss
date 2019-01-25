package com.trotyzyq.test;

import com.alibaba.fastjson.JSON;
import com.trotyzyq.config.OssClientConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import com.trotyzyq.util.MyFileUtil;
import com.trotyzyq.util.RsaSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试Controller
 * @author zyq
 */
@RestController
public class TestController {
    /** 客户端配置 **/
    @Autowired
    private OssClientConfiger ossClientConfiger;

    @Autowired
    private MyFileUtil myFileUtil;
    /** 测试提交**/
    @RequestMapping("/test1")
    public String test(MultipartFile multipartFile){
        List<String> pathList = null;
        try {
            String fileName = multipartFile.getOriginalFilename();
            FileDataEntity fileDataEntity = new FileDataEntity(fileName, multipartFile.getInputStream());
            List<FileDataEntity> flleList = new ArrayList<>();
            for(int i=0;i<2;i++){
                flleList.add(fileDataEntity);
            }
            pathList = myFileUtil.uploadFileWithForm(ossClientConfiger.getOssServerUrl(),flleList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList.toString();
    }

    /** 测试删除**/
    @RequestMapping("/test2")
    public boolean test(String path){
        boolean s2 = myFileUtil.deleteFile(ossClientConfiger.getDeleteServerUrl() + path, path);
        System.out.println(s2);
        return s2;
    }
}
