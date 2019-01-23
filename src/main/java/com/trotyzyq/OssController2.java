package com.trotyzyq;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * oss controller
 * @author zyq
 */
@RequestMapping("/fileService")
@RestController
public class OssController2 {

    /** 配置类**/
    @Autowired
    private FileConfiger fileConfiger;

    /**
     * 二进制文件上传文件
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request){
        ServletInputStream servletInputStream = null;
        String path = "";
        try {
            String date = TimeUtil.getNowDate();
            String dicPath = fileConfiger.getPathDirectory() +  date + "/";
            File file = new File(dicPath);
            if(!file.exists()){
                file.mkdir();
            }
            /** 生成随机数文件并写入到文件夹**/
            path = dicPath + TimeUtil.getCurrentTimeString().replaceAll("\\s","")
                    .replaceAll("-","").replaceAll("//","")
                    .replaceAll(":","");
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            servletInputStream = request.getInputStream();
            IOUtils.copy(servletInputStream,fileOutputStream);

            /** 保存成功记录到文件记录文件**/
            File recordFile = new File(fileConfiger.getOssRecordPath());
            OutputStream os = new FileOutputStream(recordFile);
            IOUtils.write(path + "成功上传",os, "utf-8");
        } catch (IOException e) {
        }
        return path;
    }

    /**
     * 表单上传文件
     * @param request
     * @param multipartFile 文件
     * @return String
     */
    @RequestMapping(value = "/uploadFile2",method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request, MultipartFile multipartFile){
        /** 获取后缀名 **/
        String fileName = multipartFile.getOriginalFilename();
        String[] patten = fileName.split("\\.");
        String endPatten =  patten[patten.length - 1];
        InputStream servletInputStream = null;
        String path = "";
        try {
            String date = TimeUtil.getNowDate();
            String dicPath = fileConfiger.getPathDirectory() +  date + "/";
            File file = new File(dicPath);
            if(!file.exists()){
                file.mkdir();
            }
            /** 生成随机数文件并写入到文件夹**/
            path = dicPath + TimeUtil.getCurrentTimeString().replaceAll("\\s","")
                    .replaceAll("-","").replaceAll("//","")
                    .replaceAll(":","")  + "." + endPatten;
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            servletInputStream = multipartFile.getInputStream();
            IOUtils.copy(servletInputStream,fileOutputStream);

            /** 保存成功记录到文件记录文件**/
            File recordFile = new File(fileConfiger.getOssRecordPath());
            OutputStream os = new FileOutputStream(recordFile);
            IOUtils.write(path + "成功上传",os, "utf-8");
        } catch (IOException e) {
        }

        return path;
    }

    /**
     * 获取文件
     * @param response
     * @param path 文件路径，不包含服务器
     * @throws IOException
     */
    @RequestMapping(value = "/upload/{date}/{path:.*}",method = RequestMethod.GET)
    public void getFile(HttpServletResponse response, @PathVariable("date") String date,
                        @PathVariable("path") String path){
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            path = fileConfiger.getPathDirectory() + date + "/" + path;
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
     * @throws IOException
     */
    @RequestMapping(value = "/delete/upload/{date}/{path:.*}",method = RequestMethod.GET)
    public boolean deleteFile( @PathVariable("date") String date,
                            @PathVariable("path") String path) {
        path = fileConfiger.getPathDirectory() + date + "/" + path;
        boolean success = false;
        File deleteFile = new File(path);
        if(deleteFile.exists() && deleteFile.isFile()){
            success = deleteFile.delete();
        }
        return success;
    }

    /**
     * 下载文件
     * @param response
     * @param path 文件路径，不包含服务器路径
     * @throws IOException
     */
    @RequestMapping(value = "/download/upload/{date}/{path:.*}",method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response,@PathVariable("date") String date,
                             @PathVariable("path") String path){
        path = fileConfiger.getPathDirectory() + date + "/" + path;
        response.setHeader("Content-Disposition", "attachment;Filename=" + path);
        try {
            FileInputStream fileInputStream =new FileInputStream(new File(path));
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fileInputStream,outputStream);
            /** 保存成功记录到文件记录文件**/
            File recordFile = new File(fileConfiger.getOssRecordPath());
            OutputStream os = new FileOutputStream(recordFile);
            IOUtils.write(path + "成功下载",os, "utf-8");
        }catch (Exception e){

        }

    }
}
