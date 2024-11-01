package com.example.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .status(AppConstant.SUCCESS)
                .message(AppConstant.SUCCESS_MESSAGE)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return BaseResponse.<T>builder()
                .code(code)
                .status(AppConstant.ERROR)
                .message(message)
                .build();
    }
}
