package hk.fish.fishpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.User;
import hk.fish.fishpicturebackend.service.UserService;
import hk.fish.fishpicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-02-27 16:14:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




