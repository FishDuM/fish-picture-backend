package hk.fish.fishpicturebackend.common;

import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.StatusCode;
import lombok.Data;

import java.io.Serializable;

// 自定义基础返回封装类
@Data
public class BaseResponse<T> implements Serializable {
    // 返回给前端的状态码
    private int code;
    // 返回给前端的数据
    private T data;
    // 返回给前端的信息
    private String message;

    /**
     * 全自定义基础返回封装
     * @param code 自定义返回状态码
     * @param data 自定义返回数据
     * @param message 自定义返回信息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 根据状态码的基础返回封装
     * @param code 状态码
     * @param data 自定义返回数据
     */
    public BaseResponse(StatusCode code, T data){
        this(code.getCode(), data, code.getMessage());
    }

    /**
     * 根据状态码和返回信息的基础返回封装
     * @param code 状态码
     * @param data 自定义返回数据
     * @param message 自定义返回信息
     */
    public BaseResponse(StatusCode code, T data, String message){
        this(code.getCode(), data, message);
    }

    /**
     * 快速返回成功数据工具方法
     * @param data 数据
     * @return 返回的数据
     * @param <T> 返回的数据的类型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse(0, data, StatusCode.SUCCESS.getMessage());
    }

    /**
     * 仅供全局异常处理器返回失败数据
     * @param exception 自定义异常
     * @return 包装后的异常
     */
    public static BaseResponse error(BusinessException exception){
        return new BaseResponse(exception.getCode(), null, exception.getMessage());
    }

    /**
     * 仅供全局异常处理器返回失败数据
     * @param exception 运行时异常
     * @return 包装后的异常
     */
    public static BaseResponse error(RuntimeException exception){
        return new BaseResponse(StatusCode.SERVER_ERROR, null, "系统错误");
    }
}
