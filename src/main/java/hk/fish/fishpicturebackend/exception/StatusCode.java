package hk.fish.fishpicturebackend.exception;

import lombok.Getter;

// 自定义状态码
@Getter
public enum StatusCode {

    SUCCESS(200000, "ok"),
    PARAM_ERROR(400000, "参数错误"),
    UN_LOGIN_ERROR(401000, "未登录"),
    FORBIDDEN_ERROR(403000, "无权限"),
    NOT_FOUND_ERROR(404000, "资源不存在"),
    SERVER_ERROR(500000, "服务器异常");

    // 错误码
    private final int code;

    // 错误信息
    private final String message;


    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
