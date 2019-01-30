package com.trotyzyq.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trotyzyq.config.OssClientConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author zyq on 2018/11/1.
 * 操作oss文件的工具类
 */
@Component
public class MyFileUtil {

    /** 客户端配置类**/
    @Autowired
    private OssClientConfiger ossClientConfiger;


    /**
     * 删除文件
     * @param url 删除地址
     * @param pathJson 路径
     * 成功true 失败false
     */
    public  boolean deleteFile(String url,String pathJson){
        Map map = new HashMap<>();
        map .put("timeStamp",System.currentTimeMillis() + "");
        map.put("token", ossClientConfiger.getToken());
        map.put("pathJson",pathJson);
        /** 生成参数 **/
        String mapStr = RsaSignature.getSignCheckContentV2(map);

        /** 加签 **/
        String sign = RsaSignature.rsa256Sign(mapStr, ossClientConfiger.getClientPrivateKey());
        map.put("sign",sign);

        /** 加密 **/
        String msgSignStr = JSON.toJSONString(map);
        String enStr = RsaSignature.rsaEncrypt(msgSignStr,ossClientConfiger.getServerPublicKey());


        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("params",enStr);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            JSONObject jsonObject = JSON.parseObject(result);
            if(jsonObject.getInteger("code") == 1){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 表单提交文件
     * @param url 提交服务器路径
     * @param uploadSubPath 子路径
     * @param fileList 文件
     * @return 成功：全路径，失败null
     */
    public  List<String> uploadFileWithForm(String url,String uploadSubPath, List<FileDataEntity> fileList) {
        /** 获取所有的文件名**/
        StringBuilder fileNames = new StringBuilder();
        for(int i = 0 ;i < fileList.size(); i++){
            String fileName = fileList.get(i).getFileName();
            fileNames.append(fileName + ";");
        }

        Map map = new HashMap<>();
        map .put("timeStamp",System.currentTimeMillis() + "");
        map.put("token", ossClientConfiger.getToken());
        map.put("uploadSubPath", uploadSubPath);
        map.put("fileNames", fileNames.toString());

        /** 生成参数 **/
        String mapStr = RsaSignature.getSignCheckContentV2(map);

        /** 加签 **/
        String sign = RsaSignature.rsa256Sign(mapStr, ossClientConfiger.getClientPrivateKey());
        map.put("sign",sign);

        /** 加密 **/
        String msgSignStr = JSON.toJSONString(map);
        String enStr = RsaSignature.rsaEncrypt(msgSignStr,ossClientConfiger.getServerPublicKey());


        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);


            for(int i =0 ;i < fileList.size() ;i++){
                FileDataEntity fileDataEntity = fileList.get(i);
                String fileName = fileDataEntity.getFileName();
                builder.addBinaryBody("multipartFiles" + i,fileDataEntity.getFileInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);
            }
            builder.addTextBody("params",enStr);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            /** 解析请求**/
            JSONObject jsonObject = JSON.parseObject(result);
            if(jsonObject.getInteger("code") == 1){
                JSONArray pathArray = jsonObject.getJSONObject("data").getJSONArray("path");
                List pathList = pathArray.subList(0,pathArray.size());
                return pathList;
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
