package com.classroom.common;

/**
 * 响应状态码
 */
public final class ResultCode {

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 参数错误 */
    public static final int BAD_REQUEST = 400;

    /** 未登录 */
    public static final int UNAUTHORIZED = 401;

    /** 无权限 */
    public static final int FORBIDDEN = 403;

    /** 资源不存在 */
    public static final int NOT_FOUND = 404;

    /** 业务错误 */
    public static final int ERROR = 500;

    private ResultCode() {
    }
}
