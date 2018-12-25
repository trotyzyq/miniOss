package com.trotyzyq;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @Author zyq on 2018/11/1.
 * 文件传输Controller
 */
@RequestMapping("/oss")
@RestController
public class OssController {

    /**
     * 二进制文件上传文件
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request,HttpServletResponse response){
        String pathName = "";
        Result result = new Result();
        ServletInputStream servletInputStream = null;
        try {
            /** 生成随机数文件并写入到文件夹**/
            String randomUUID = UUID.randomUUID().toString() + "-" + TimeUtil.getCurrentTimeString();
            pathName = FileConstant.PATH_DIRECTORY + randomUUID;
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pathName));
            byte[] b =new byte[1024];
            int flag;
            servletInputStream = request.getInputStream();
            while(( flag = servletInputStream.read(b)) != -1){
                fileOutputStream.write(b,0,flag);
            }
            result.setCode(1);
            result.setPathName(FileConstant.GETFILE_PATH_URL+pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  JSON.toJSONString(result);
    }

    /**
     * 表单上传文件
     * @param request
     * @param response
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadFile2",method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file){
        String pathName = "";
        String fileName = file.getOriginalFilename();
        String[] patten = fileName.split("\\.");
        String endPatten =  patten[patten.length - 1];
        Result result = new Result();
        InputStream servletInputStream = null;
        try {
            /** 生成随机数文件并写入到文件夹**/
            String randomUUID = UUID.randomUUID().toString() + "-" + TimeUtil.getCurrentTimeString();
            pathName = FileConstant.PATH_DIRECTORY + randomUUID + "." + endPatten;
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pathName));
            byte[] b =new byte[1024];
            int flag;
            servletInputStream = file.getInputStream();
            while(( flag = servletInputStream.read(b)) != -1){
                fileOutputStream.write(b,0,flag);
            }
            result.setCode(1);
            result.setPathName(pathName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  JSON.toJSONString(result);
    }

    @RequestMapping(value = "/getFile",method = RequestMethod.GET)
    public void getFile(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        FileInputStream fileInputStream =new FileInputStream(new File(path));
        OutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int length;
        while((length = fileInputStream.read(b)) != -1){
            outputStream.write(b,0,length);
        }
        fileInputStream.close();
        outputStream.close();
    }

    @RequestMapping(value = "/deleteFile",method = RequestMethod.GET)
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        File deleteFile = new File(path);
        if(deleteFile.exists() && deleteFile.isFile()){
            deleteFile.delete();
        }
    }

    @RequestMapping(value = "/downloadFile")
    public void downloadFile(HttpServletResponse response, String path) throws IOException {
        response.setHeader("Content-Disposition", "attachment;Filename=" + path);
        FileInputStream fileInputStream =new FileInputStream(new File(path));
        OutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int length;
        while((length = fileInputStream.read(b)) != -1){
            outputStream.write(b,0,length);
        }
        fileInputStream.close();
        outputStream.close();
    }


}
