package com.example.Response;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//The below tag ensures the order in which the resposne should look in network tab
@JsonPropertyOrder ({ "success", "status", "message", "timestamp", "success", "data",  })
public class ApiResponse<T>{
    private int status;
    private String message;
    private T data;
    private Long timeStamp = Instant.now().toEpochMilli();
    private boolean success;

    //Parameterized Constructor
    private ApiResponse(HttpStatus status, String message, T data)
    {
        this.status = status.value();
        this.message = message;
        this.data = data;   
        this.success = status.is2xxSuccessful();
    }
 
    public static <T> ApiResponse<T> response(HttpStatus status, String message, T data)
    {
        return new ApiResponse<T>(status, message, data);
    }
    //-------------------Getters and Setters--------------------//
    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Long getTimestamp() { return timeStamp; }
    
}
