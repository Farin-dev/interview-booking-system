package com.example.interview_scheduler.response;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private String message;
    private T data;
    private int statusCode;

    public static <T> BaseResponse<T> success(String message, T data, int statusCode) {
        return new BaseResponse<>(message, data, statusCode);
    }

    public static <T> BaseResponse<T> failure(String message, int statusCode) {
        return new BaseResponse<>(message, null, statusCode);
    }
}
