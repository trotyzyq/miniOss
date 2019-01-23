package com.trotyzyq.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @Author zyq on 2018/11/1.
 * 操作oss文件的工具类
 */
public class MyFileUtil {

    /**
     * 文件流上传文件，不包含其他参数
     * @param url 上传地址
     * @param file 文件
     * @return 成功：全路径
     */
    public static String uploadFile(String url, MultipartFile file)  {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            InputStreamEntity inputEntry = new InputStreamEntity(file.getInputStream());
            httpPost.setEntity(inputEntry);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            /** 上传结果解析**/
            String uploadResult = EntityUtils.toString(httpEntity,"utf-8");
            response.close();
            httpClient.close();
            JSONObject jsonObject = JSON.parseObject(uploadResult);
            if(jsonObject.getInteger("code") == 1){
                return jsonObject.getJSONObject("data").getString("path");
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }finally{
            try {
                if(httpPost!=null){
                    httpPost.releaseConnection();
                }
                if(httpClient!=null){
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 删除文件
     * @param url 删除地址
     * @param params 其他参数包括token
     */
    public static boolean deleteFile(String url,Map<String, String> params){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (String pKey : params.keySet()) {
                builder.addTextBody(pKey,params.get(pKey));
            }
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
     * @param file 文件
     * @param paramsMap 其他参数
     * @return
     */
    public static String uploadFile(String url, MultipartFile file, Map<String,String> paramsMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            String fileName = file.getOriginalFilename();
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("multipartFile", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            for (String pKey : paramsMap.keySet()) {
                builder.addTextBody(pKey,paramsMap.get(pKey));
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            JSONObject jsonObject = JSON.parseObject(result);
            if(jsonObject.getInteger("code") == 1){
                return jsonObject.getJSONObject("data").getString("path");
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
