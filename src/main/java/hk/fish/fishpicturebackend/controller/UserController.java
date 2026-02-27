package hk.fish.fishpicturebackend.controller;

import hk.fish.fishpicturebackend.common.BaseResponse;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
}
