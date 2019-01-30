## 介绍
该软件基于springboot开发,支持文件上传，文件读取，文件读取,文件下载。
不支持https,若需要可自行配置nginx反向代理即可支持。

## 特点
### 安全机制
软件实现支付中常用的加密签名机制，确保文件不会被修改。
### 批量上传、删除
利用软件源码中的工具类，可以轻松实现文件的批量上传和删除。同时对这两种操作进行了优化，批量操作失败的情况下，会将垃圾文件进行删除。
### 效率提高
利用SpringBoot-web的功能，配置了线程池和限制上传文件大小，提高效率。
### 不支持https
被套路了吧，哈哈。当然如果需要，可自行配置nginx反向代理即可支持，证书的话自己想办法。。。

## 软件使用
### 创建根目录文件夹

### 安装环境
1. 安装jdk即可，7或者8都行。
2. 安装`gradle`，如果不想安装`gradle`,只想用`maven`，就复制里面的源码吧~~~
### 下载源码
1. download或者clone源码，打包成jar包

### 修改yml配置文件
```
ossServer:   //服务端配置
  pathDirectory: /upload/  需要保存的服务器根目录
  ossRecordPath: /upload/record.log  需要保存的记录文件，相当于数据库啦！
  token: 3dsf43cvxe35cxdfg   //验证参数
  serverPrivateKey:   //服务端私钥
  serverPublicKey: //服务端公钥
  clientPublicKey: //客户端公钥
ossClient:
  ossServerUrl: http://localhost:8080/uploadFile  //上传文件地址
  deleteServerUrl : http://localhost:8080/delete  //删除文件地址
  visitPath: http://localhost:8080/getFile?path=  //获取文件地址
  token : 3dsf43cvxe35cxdfg        //token地址
  clientPrivateKey:   //客户端私钥
  clientPublicKey: //客户端公钥
  serverPublicKey:  服务端公钥
```
<font color=red>备注1</font>：你上面看到的`ossClient` 这一部分是在客户端配置的，谨记。配置在这个软件里只是方便测试。  
<font color=red>备注2</font>: 公私钥的获取可以利用蚂蚁金服的工具，非常方便，支持windows和mac [链接在此，谁敢点我](https://docs.open.alipay.com/291/105971)

### 使用工具类 -FileUtil
<font color=red>具体可以查看TestController进行测试</font>
#### 上传文件
```
String fileName = m1.getOriginalFilename();
FileDataEntity fileDataEntity = new FileDataEntity(fileName, m1.getInputStream());
List<FileDataEntity> flleList = new ArrayList<>();
flleList.add(fileDataEntity);
pathList = myFileUtil.uploadFileWithForm(ossClientConfiger.getOssServerUrl(),"ups/age",flleList);
```

#### 删除文件
```
@RequestMapping("/test2")
public boolean test(String path1, String path2 , String path3, String path4){
    JSONArray jsonArray = new JSONArray();
    jsonArray.add(path1);
    jsonArray.add(path2);
    jsonArray.add(path3);
    String pathJson = jsonArray.toJSONString();
    boolean s2 = myFileUtil.deleteFile(ossClientConfiger.getDeleteServerUrl(), pathJson);
    System.out.println(s2);
    return s2;
}
```

#### 文件读取
类似于img 的src属性   
get http://www.insistself.cn:8080/getFile  
参数:path  注释:文件路径  

### 下载
浏览器提交  
http://www.insistself.cn:9002/download  
参数:path 注释:文件路径  