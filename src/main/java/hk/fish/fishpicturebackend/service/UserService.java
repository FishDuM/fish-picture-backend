package hk.fish.fishpicturebackend.service;

import hk.fish.fishpicturebackend.domain.dto.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-02-27 16:14:01
*/
public interface UserService extends IService<User> {

    long userRegister(UserRegisterRequest userRegisterRequest);

    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    // 获取当前登录用户
    LoginUserVO getCurrentUser(HttpServletRequest request);

    // 系统内部使用获取当前用户
    User getCurrentUserInSystem(HttpServletRequest request);
}
