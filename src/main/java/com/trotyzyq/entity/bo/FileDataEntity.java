package com.trotyzyq.entity.bo;

import java.io.InputStream;

/** 文件数据实体 */
public class FileDataEntity {
	private String fileName;  //文件名称
	private InputStream fileInputStream;  //文件流
	
	public FileDataEntity(String fileName, InputStream fileInputStream){
		this.fileName = fileName;
		this.fileInputStream = fileInputStream;
	}
	
	/** 文件名称 */
	public String getFileName() {
		return fileName;
	}
	
	/** 文件名称 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/** 文件流 */
	public InputStream getFileInputStream() {
		return fileInputStream;
	}
	
	/** 文件流 */
	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}
	
}