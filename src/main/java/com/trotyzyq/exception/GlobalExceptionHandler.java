package com.trotyzyq.exception;

import com.trotyzyq.entity.bo.JsonObjectBO;
import com.trotyzyq.entity.bo.ResponseCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler
    public JsonObjectBO unknownException(MultipartException e) {
        return new JsonObjectBO(ResponseCode.INVALID_INPUT,"上传文件过大,请小于100MB",null);
    }
}
