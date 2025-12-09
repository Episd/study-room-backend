package com.nbucs.studyroombackend.exception;

import com.nbucs.studyroombackend.dto.response.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ServiceException.class)
    public Response<Void> handleServiceException(ServiceException e) {
        // TODO: 添加异常的日志处理
        return Response.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        return Response.error(500, e.getMessage());
    }
}
