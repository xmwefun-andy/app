package com.bootvue.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    PARAM_ERROR(400, "非法参数"),
    ACCESS_DENY(403, "没有权限"),
    DEFAULT(600, "系统异常"),
    LOGIN_ERROR(601, "用户名或密码错误"),
    TOKEN_ERROR(602, "凭证无效"),
    REFRESH_TOKEN_ERROR(603, "refresh token无效");
    private final Integer code;
    private final String msg;
}
