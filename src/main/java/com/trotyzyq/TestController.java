package com.trotyzyq;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @RequestMapping("/test1")
    public String test(MultipartFile multipartFile){

        String  localFileName = multipartFile.getOriginalFilename();

        Map<String, ContentBody> reqParam = new HashMap<String,ContentBody>();
        reqParam.put("Filename", new StringBody(localFileName, ContentType.MULTIPART_FORM_DATA));
        reqParam.put("pictitle", new StringBody(localFileName, ContentType.MULTIPART_FORM_DATA));
        reqParam.put("dir", new StringBody("upload1", ContentType.MULTIPART_FORM_DATA));
        reqParam.put("fileNameFormat", new StringBody("{time}{rand:6}", ContentType.MULTIPART_FORM_DATA));
        reqParam.put("fileName", new StringBody(localFileName, ContentType.MULTIPART_FORM_DATA));
        reqParam.put("fileName", new StringBody(localFileName, ContentType.MULTIPART_FORM_DATA));
//        reqParam.put("upfile", new FileBody(new File(fileLocation)));
        reqParam.put("Upload", new StringBody("Submit Query", ContentType.MULTIPART_FORM_DATA));

        multipartFile.get

        String s1 = null;
        try {
            s1 = MyFileUtil.postFileMultiPart(FileConstant.UPLOAD_PATH,reqParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(s1);
        return s1;
    }

    @RequestMapping("/test2")
    public String test(String path ){
        String s2 = MyFileUtil.deleteFile(path);
        System.out.println(s2);
        return s2;
    }
}
