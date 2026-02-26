package hk.fish.fishpicturebackend.exception;

import lombok.Getter;

// 自定义运行时异常
@Getter
public class BusinessException extends RuntimeException{
    // 错误码
    private final int code;
    // 自定义错误码和错误消息
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    // 从自定义状态码取出状态码和消息
    public BusinessException(StatusCode code) {
        super(code.getMessage());
        this.code = code.getCode();
    }
    // 使用状态码但是自己传递消息
    public BusinessException(StatusCode code, String message) {
        super(message);
        this.code = code.getCode();
    }
}
