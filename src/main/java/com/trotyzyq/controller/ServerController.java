package com.trotyzyq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trotyzyq.config.OssServerConfiger;
import com.trotyzyq.entity.bo.FileDataEntity;
import com.trotyzyq.entity.bo.JsonObjectBO;
import com.trotyzyq.entity.bo.ResponseCode;
import com.trotyzyq.service.IServerService;
import com.trotyzyq.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * oss controller
 * @author zyq
 */
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
     * 表单上传文件
     * @param uploadSubPath 保存路径
     * @param params 验证参数
     * @return JsonObjectBO
     */
    @RequestMapping(value = "/upload-file",method = RequestMethod.POST)
    public JsonObjectBO uploadFile(HttpServletRequest request, String uploadSubPath, String token, String params){
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

        if(fileList.size() == 0 || StringUtil.isNull(uploadSubPath)|| StringUtil.isNull(token)){
            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "参数错误",null);
        }

//        /** 判断解密是否成功**/
//        String decryptMsg = RsaSignature.rsaDecrypt(params,ossServerConfiger.getServerPrivateKey());
//        if(StringUtil.isNull(decryptMsg)){
//            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "解密错误",null);
//        }

//        Map map = JSON.parseObject(decryptMsg, Map.class);
//        boolean deSignSuccess  = RsaSignature.rsaCheckV2(map,ossServerConfiger.getClientPublicKey());
//
//        /** 判断解签是否相同 **/
//        if(! deSignSuccess){
//             return new JsonObjectBO(ResponseCode.INVALID_INPUT, "解签失败",null);
//        }
//        if(! map.get("token").equals(ossServerConfiger.getToken())){
//            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "token无效",null);
//        }
        if(!token.equals(ossServerConfiger.getToken())){
            return new JsonObjectBO(ResponseCode.INVALID_INPUT, "token无效",null);
        }

        List<String> saveList = serverService.saveFile(fileList, uploadSubPath);
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
    @RequestMapping(value = "/getFile",method = RequestMethod.GET)
    public void getFile(HttpServletResponse response,String path){
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
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
     * @param pathJson 文件路径json
     * @return  JsonObjectBO
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public String deleteFile( String pathJson, String params) {
        if(StringUtil.isNull(params) || StringUtil.isNull(pathJson)){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "参数错误",null));
        }
        JSONArray pathArray = null;
        try {
            pathArray = JSON.parseArray(pathJson);
        }catch (Exception e){
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "json参数异常",null));
        }

//        /** 判断解密是否成功**/
//        String decryptMsg = RsaSignature.rsaDecrypt(params,ossServerConfiger.getServerPrivateKey());
//        if(StringUtil.isNull(decryptMsg)){
//            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "解密错误",null));
//        }
//        Map map = JSON.parseObject(decryptMsg, Map.class);
//        boolean deSignSuccess  = RsaSignature.rsaCheckV2(map,ossServerConfiger.getClientPublicKey());
//
//        /** 判断解签是否相同 **/
//        if(! deSignSuccess){
//            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "解签失败",null));
//        }
//        /** 判断token是否相同 **/
//        if(! map.get("token").equals(ossServerConfiger.getToken())){
//            return JSON.toJSONString(new JsonObjectBO(ResponseCode.INVALID_INPUT, "token无效",null));
//        }


        boolean success = false;
        List<String> copyList = new ArrayList<>();
        FileOutputStream fileOutputStream = null;
        try {
            for(int i = 0; i < pathArray.size(); i++){
                String path = pathArray.getString(i);
                File deleteFile = new File(path);
                String copyPath = path + ".copy";
                /** 将复制的保存下来**/
                copyList.add(copyPath);


                /** 删除之前先复制一份**/
                FileInputStream fileInputStream = new FileInputStream(new File(path));
                fileOutputStream = new FileOutputStream(new File(copyPath));
                IOUtils.copy(fileInputStream, fileOutputStream);

                if(deleteFile.exists() && deleteFile.isFile()){
                    success = deleteFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            success  = false;
        }

        /** 如果删除失败，还原**/
        if(!success){
            logger.info(JSON.toJSONString(pathArray) + "删除失败");
            for(int i =0 ;i< copyList.size();i++){
                String copyPath = copyList.get(i);
                String path = copyPath.substring(0,copyPath.length()-5);

                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(new File(copyPath));
                    fileOutputStream = new FileOutputStream(new File(path));
                    IOUtils.copy(fileInputStream, fileOutputStream);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /** 还原之后将copy的删除**/
            for(int i =0 ;i< copyList.size();i++){
                String copyPath = copyList.get(i);

                File file = new File(copyPath);
                if(file.exists() && file.isFile()){
                    success = file.delete();
                }
            }
            return JSON.toJSONString(new JsonObjectBO(ResponseCode.SERVER_ERROR, "删除失败",null));
        }

        /** 如果成功，将复制的全部删除**/
        for(int i =0 ;i< copyList.size();i++){
            String copyPath = copyList.get(i);

            File file = new File(copyPath);
            if(file.exists() && file.isFile()){
                success = file.delete();
            }
        }

        logger.info(JSON.toJSONString(pathArray) + "删除成功");
        return JSON.toJSONString(new JsonObjectBO(ResponseCode.NORMAL, "删除成功",null));
    }

    /**
     * 下载文件
     * @param response
     * @param path 文件路径，不包含服务器路径
     */
    @RequestMapping(value = "/download/",method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response,String path){
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
