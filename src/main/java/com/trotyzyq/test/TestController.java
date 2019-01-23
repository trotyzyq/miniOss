package com.trotyzyq.test;

import com.trotyzyq.config.ClientConfiger;
import com.trotyzyq.util.MyFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试Controller
 * @author zyq
 */
@RestController
public class TestController {
    /** 客户端配置 **/
    @Autowired
    private ClientConfiger clientConfiger;

    /** 测试提交**/
    @RequestMapping("/test1")
    public String test(MultipartFile multipartFile){
        Map map = new HashMap<>();
        map.put("token",clientConfiger.getToken());
        String x = MyFileUtil.uploadFile(clientConfiger.getOssServerUrl(),multipartFile ,map);
        return x;
    }

    /** 测试删除**/
    @RequestMapping("/test2")
    public boolean test(String path ){
        Map map = new HashMap<>();
        map.put("token",clientConfiger.getToken());
        boolean s2 = MyFileUtil.deleteFile(clientConfiger.getDeleteServerUrl() + path, map);
        System.out.println(s2);
        return s2;
    }
}
