package hk.fish.fishpicturebackend.exception;

// 异常处理工具类
public class ThrowUtils {
    /**
     * 条件成立则抛异常
     * @param condition 条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException){
        if(condition){
            throw runtimeException;
        }
    }

    /**
     * 条件成立则根据状态码抛自定义异常
     * @param condition 条件
     * @param code 状态码
     */
    public static void throwIf(boolean condition, StatusCode code){
        throwIf(condition, new BusinessException(code));
    }

    /**
     * 条件成立则根据状态码和信息抛自定义异常
     * @param condition 条件
     * @param code 状态码
     * @param message 自定义异常信息
     */
    public static void throwIf(boolean condition, StatusCode code, String message){
        throwIf(condition, new BusinessException(code, message));
    }

    /**
     * 根据状态码直接抛异常
     * @param code 状态码
     */
    public static void error(StatusCode code){
        throw new BusinessException(code);
    }

    /**
     * 根据状态码和自定义信息直接抛异常
     * @param code 状态码
     * @param message 自定义异常信息
     */
    public static void error(StatusCode code, String message){
        throw new BusinessException(code, message);
    }
}
