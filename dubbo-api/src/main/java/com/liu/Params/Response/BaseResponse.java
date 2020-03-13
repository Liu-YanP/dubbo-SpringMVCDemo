package com.liu.Params.Response;

public class BaseResponse<T> {

    private int code;
    private String msg;
    private T data;

    private BaseResponse(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private BaseResponse(String msg){
        this.code = 1;
        this.msg = msg;
    }

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(data);
    }

    public static <T> BaseResponse<T> error(String msg){
        return new BaseResponse<T>(msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
