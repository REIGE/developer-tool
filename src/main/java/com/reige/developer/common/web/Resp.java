package com.reige.developer.common.web;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * 返回数据保障类
 */
@Data
@Builder
@Accessors(chain = true)
public class Resp<T> {
    private Integer code = HttpStatus.OK.value();

    private String msg;

    private T data;

    private Object other;

    private boolean success = true;

    public static <T> Resp ok(T data) {
        return Resp.builder().data(data).success(true).build();
    }

    public static <T> Resp ok(T data, Object other) {
        return Resp.builder().data(data).success(true).other(other).build();
    }

    public static Resp ok() {
        return ok(null);
    }

    public static Resp error(String msg) {
        return Resp.builder().success(false).msg(msg).build();
    }
}

