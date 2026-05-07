package com.compare.core.result;

import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, 200, "success");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "success");
    }

    public static <T> R<T> ok(T data, String message) {
        return restResult(data, 200, message);
    }

    public static <T> R<T> failed() {
        return restResult(null, 500, "failed");
    }

    public static <T> R<T> failed(String message) {
        return restResult(null, 500, message);
    }

    public static <T> R<T> failed(int code, String message) {
        return restResult(null, code, message);
    }

    private static <T> R<T> restResult(T data, int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMessage(message);
        return r;
    }
}
