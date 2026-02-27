package hk.fish.fishpicturebackend.service;

import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-02-27 16:14:01
*/
public interface UserService extends IService<User> {

    long userRegister(UserRegisterRequest userRegisterRequest);

}
