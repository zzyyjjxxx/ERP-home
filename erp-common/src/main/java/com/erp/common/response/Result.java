package com.erp.common.response;

import lombok.Data;
import java.util.UUID;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "操作成功";
        r.data = data;
        r.traceId = UUID.randomUUID().toString().replace("-", "");
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.traceId = UUID.randomUUID().toString().replace("-", "");
        return r;
    }

    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }
}
