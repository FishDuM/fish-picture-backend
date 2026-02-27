package hk.fish.fishpicturebackend.controller;

import hk.fish.fishpicturebackend.annotation.AuthCheck;
import hk.fish.fishpicturebackend.common.BaseResponse;
import hk.fish.fishpicturebackend.domain.dto.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.enums.UserRole;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;
import hk.fish.fishpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static hk.fish.fishpicturebackend.common.BaseCode.ADMIN;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求类
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return BaseResponse.success(userService.userRegister(userRegisterRequest));
    }

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求类
     * @return 用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return BaseResponse.success(userService.userLogin(userLoginRequest, request));
    }

    /**
     * 获取当前登录用户
     * @param request 请求
     * @return 当前用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        return BaseResponse.success(userService.getCurrentUser(request));
    }

    /**
     * 用户登出
     * @param request 登出请求
     * @return 登出成功
     */
    @PostMapping("/logout")
    public BaseResponse<String> userLogout(HttpServletRequest request) {
        userService.userLogout(request);
        return BaseResponse.success("登出成功");
    }
}
