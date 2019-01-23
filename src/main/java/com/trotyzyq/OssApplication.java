package com.trotyzyq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OssApplication {

	static Logger logger= LoggerFactory.getLogger(OssApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(OssApplication.class, args);
		logger.error("启动了");
	}
}
