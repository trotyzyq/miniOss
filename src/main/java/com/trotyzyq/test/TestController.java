package com.trotyzyq.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trotyzyq.config.OssClientConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import com.trotyzyq.util.MyFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public String test(MultipartFile m1, MultipartFile m2 , MultipartFile m3){
        List<String> pathList = null;
        try {
            String fileName = m1.getOriginalFilename();
            FileDataEntity fileDataEntity = new FileDataEntity(fileName, m1.getInputStream());
            List<FileDataEntity> flleList = new ArrayList<>();
            flleList.add(fileDataEntity);

            fileName = m2.getOriginalFilename();
            fileDataEntity = new FileDataEntity(fileName, m1.getInputStream());
            flleList.add(fileDataEntity);

            fileName = m3.getOriginalFilename();
            fileDataEntity = new FileDataEntity(fileName, m1.getInputStream());
            flleList.add(fileDataEntity);
//
//            fileName = m4.getOriginalFilename();
//            fileDataEntity = new FileDataEntity(fileName, m1.getInputStream());
//            flleList.add(fileDataEntity);

            pathList = myFileUtil.uploadFileWithForm(ossClientConfiger.getOssServerUrl(),"ups/age",flleList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(pathList);
    }

    /** 测试删除**/
    @RequestMapping("/test2")
    public boolean test(String path1, String path2 , String path3, String path4){
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(path1);
        jsonArray.add(path2);
        jsonArray.add(path3);
//        jsonArray.add(path4);
        String pathJson = jsonArray.toJSONString();
        boolean s2 = myFileUtil.deleteFile(ossClientConfiger.getDeleteServerUrl(), pathJson);
        System.out.println(s2);
        return s2;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/Users/zyq/Desktop/timg.jpeg");
        FileInputStream fileInputStream = new FileInputStream(file);
        for(int i = 0;i<100; i++){

        }
    }
}
