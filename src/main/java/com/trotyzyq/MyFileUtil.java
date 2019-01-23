package com.trotyzyq;


import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
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

    public static String postFileMultiPart(String url, Map<String, ContentBody> reqParam) throws  IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            // 创建httpget.
            HttpPost httppost = new HttpPost(url);

            //setConnectTimeout：设置连接超时时间，单位毫秒。setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
            RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(15000).build();
            httppost.setConfig(defaultRequestConfig);

            System.out.println("executing request " + httppost.getURI());

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for(Map.Entry<String,ContentBody> param : reqParam.entrySet()){
                multipartEntityBuilder.addPart(param.getKey(), param.getValue());
            }
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);

            // 执行post请求.
            CloseableHttpResponse response = httpclient.execute(httppost);

            System.out.println("got response");

            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                //System.out.println("--------------------------------------");
                // 打印响应状态
                //System.out.println(response.getStatusLine());
                if (entity != null) {
                    return EntityUtils.toString(entity, Charset.forName("UTF-8"));
                }
                //System.out.println("------------------------------------");
            } finally {
                response.close();

            }
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 上传文件
     * @param file
     * @return 成功：全路径
     * @throws IOException
     */
    public static String uploadFile(MultipartFile file)  {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(FileConstant.UPLOAD_PATH);
            InputStreamEntity inputEntry = new InputStreamEntity(file.getInputStream());
            httpPost.setEntity(inputEntry);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            /** 上传结果解析**/
            String uploadResult = EntityUtils.toString(httpEntity,"utf-8");
            response.close();
            httpclient.close();
            return uploadResult;
        }catch (Exception e){
            return null;
        }

    }

    /**
     * 删除文件
     * @param path
     */
    public static String deleteFile(String path){
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
            httpGet = new HttpGet(FileConstant.DELETE_PATH);
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity,"utf-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(httpGet!=null){
                    httpGet.releaseConnection();
                }
                if(httpClient!=null){
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
