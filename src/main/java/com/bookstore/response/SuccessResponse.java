package com.bookstore.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SuccessResponse<T> extends GeneralResponse {
    private final T data;

    public SuccessResponse(T data, HttpStatus status) {
        super(status);
        this.data = data;
    }
}
