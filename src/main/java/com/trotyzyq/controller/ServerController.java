package com.trotyzyq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trotyzyq.config.OssServerConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import com.trotyzyq.entity.bo.JsonObjectBO;
import com.trotyzyq.entity.bo.ResponseCode;
import com.trotyzyq.service.IServerService;
import com.trotyzyq.util.RsaSignature;
import com.trotyzyq.util.StringUtil;
import com.trotyzyq.util.TimeUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

/**
 * oss controller
 * @author zyq
 */
@RequestMapping("/fileService")
@RestController
public class ServerController {

    /** 配置类**/
    @Autowired
    private OssServerConfiger ossServerConfiger;

    /** oss服务service**/
    @Autowired
    private IServerService serverService;

    /** 日志记录**/
    private Logger logger = LoggerFactory.getLogger(ServerController.class);

    /**
     * 二进制文件上传文件
     * @param request
     * @return JsonObjectBO
     */
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public JsonObjectBO uploadFile(HttpServletRequest request){
        ServletInputStream servletInputStream = null;
        String path = "";
        try {
            String date = TimeUtil.getNowDate();
            String dicPath = ossServerConfiger.getPathDirectory() +  date + "/";
            File file = new File(dicPath);
            if(!file.exists()){
                file.mkdir();
            }
            /** 生成随机数文件并写入到文件夹**/
            path = dicPath + TimeUtil.getCurrentTimeString().replaceAll("\\s","")
                    .replaceAll("-","").replaceAll("//","")
                    .replaceAll(":","") + UUID.randomUUID();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            servletInputStream = request.getInputStream();
            IOUtils.copy(servletInputStream,fileOutputStream);

            /** 保存成功记录到文件记录文件**/
            File recordFile = new File(ossServerConfiger.getOssRecordPath());
            OutputStream os = new FileOutputStream(recordFile);
            IOUtils.write(path + "成功上传",os, "utf-8");

            /** 保存成功 设置返回路径**/
            JSONObject pathJSON = new JSONObject();
            pathJSON.put("path",path);
            return new JsonObjectBO(ResponseCode.NORMAL, "上传文件成功",pathJSON);
        } catch (IOException e) {
            logger.error(path + "上传失败");
            return new JsonObjectBO(ResponseCode.SERVER_ERROR, "上传文件失败",null);
        }
    }

    /**
     * 表单上传文件
     * @param params 验证参数
     * @return JsonObjectBO
     */
    @RequestMapping(value = "/uploadFile2",method = RequestMethod.POST)
    public JsonObjectBO uploadFile(HttpServletRequest request, String params){
        /** 文件列表 **/
        List<FileDataEntity>  fileList = new ArrayList<>();
        try {
            Iterator<Part> iterator=request.getParts().iterator();
            while(iterator.hasNext()){
                Part part =  iterator.next();
                if(part.getSubmittedFileName() != null){
                    FileDataEntity fileDataEntity = new FileDataEntity(part.getSubmittedFileName(),part.getInputStream());
                    fileList.add(fileDataEntity);
                }
            }
        } catch (Exception e){
            return new JsonObjectBO(ResponseCode.SERVER_ERROR, "系统错误",null);
        }

        if(fileList.size() == 0 || params == null){
            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "参数错误",null);
        }

        /** 判断解密是否成功**/
        String decryptMsg = RsaSignature.rsaDecrypt(params,ossServerConfiger.getServerPrivateKey());
        if(StringUtil.isNull(decryptMsg)){
            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "解密错误",null);
        }
        Map map = JSON.parseObject(decryptMsg, Map.class);
        boolean deSignSuccess  = RsaSignature.rsaCheckV2(map,ossServerConfiger.getClientPublicKey());

        /** 判断解签是否相同 **/
        if(! deSignSuccess){
             return new JsonObjectBO(ResponseCode.INVALID_INPUT, "解签失败",null);
        }
        if(! map.get("token").equals(ossServerConfiger.getToken())){
            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "token无效",null);
        }

        List<String> saveList = serverService.saveFile(fileList);
        if(saveList.size() > 0 && saveList.size() == fileList.size()){
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(saveList);
            jsonObject.put("path",jsonArray);
            return new JsonObjectBO(ResponseCode.NORMAL, "上传成功",jsonObject);
        }
        return new JsonObjectBO(ResponseCode.SERVER_ERROR, "上传失败",null);
    }

    /**
     * 获取文件
     * @param response
     * @param path 文件路径，不包含服务器
     */
    @RequestMapping(value = "/upload/{date}/{path:.*}",method = RequestMethod.GET)
    public void getFile(HttpServletResponse response, @PathVariable("date") String date,
                        @PathVariable("path") String path){
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            path = ossServerConfiger.getPathDirectory() + date + "/" + path;
            File file = new File(path);
            if(!file.exists()){
                outputStream.write("no this file".getBytes());
                return;
            }
            FileInputStream fileInputStream =new FileInputStream(new File(path));
            IOUtils.copy(fileInputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * @param path 文件路径 不包含文件
     * @return  JsonObjectBO
     */
    @RequestMapping(value = "/delete/upload/{date}/{path:.*}",method = RequestMethod.POST)
    public String deleteFile( @PathVariable("date") String date,
                            @PathVariable("path") String path, String params) {
        if(params == null){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "参数错误",null));
        }

        /** 判断解密是否成功**/
        String decryptMsg = RsaSignature.rsaDecrypt(params,ossServerConfiger.getServerPrivateKey());
        if(StringUtil.isNull(decryptMsg)){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "解密错误",null));
        }
        Map map = JSON.parseObject(decryptMsg, Map.class);
        boolean deSignSuccess  = RsaSignature.rsaCheckV2(map,ossServerConfiger.getClientPublicKey());

        /** 判断解签是否相同 **/
        if(! deSignSuccess){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "解签失败",null));
        }
        /** 判断token是否相同 **/
        if(! map.get("token").equals(ossServerConfiger.getToken())){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "token无效",null));
        }
        /** 判断签名的信息是否与上次路径的一致**/
        path = ossServerConfiger.getPathDirectory() + date + "/" + path;
        if(!path.equals(map.get("path"))){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "文件名无效",null));
        }

        boolean success = false;
        File deleteFile = new File(path);
        if(deleteFile.exists() && deleteFile.isFile()){
            success = deleteFile.delete();
        }
        if(success){
            logger.info(path + "删除成功");
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.NORMAL, "删除成功",null));
        }
        logger.error(path + "删除失败");
        return JSON.toJSONString(new JsonObjectBO(ResponseCode.SERVER_ERROR, "删除失败",null));
    }

    /**
     * 下载文件
     * @param response
     * @param path 文件路径，不包含服务器路径
     */
    @RequestMapping(value = "/download/upload/{date}/{path:.*}",method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response,@PathVariable("date") String date,
                             @PathVariable("path") String path){
        path = ossServerConfiger.getPathDirectory() + date + "/" + path;
        response.setHeader("Content-Disposition", "attachment;Filename=" + path);
        try {
            FileInputStream fileInputStream =new FileInputStream(new File(path));
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fileInputStream,outputStream);
            /** 保存成功记录到文件记录文件**/
            File recordFile = new File(ossServerConfiger.getOssRecordPath());
            OutputStream os = new FileOutputStream(recordFile);
            IOUtils.write(path + "成功下载",os, "utf-8");
        }catch (Exception e){
            logger.error(path + "下载失败");
        }
    }
}
