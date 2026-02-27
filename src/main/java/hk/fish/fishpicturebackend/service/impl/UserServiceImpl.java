package hk.fish.fishpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRole;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.service.UserService;
import hk.fish.fishpicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-02-27 16:14:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求类
     * @return 用户id
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 提取参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 判断数据是否为空
        ThrowUtils.throwIf((userAccount == null || userPassword == null || checkPassword == null),StatusCode.PARAM_ERROR, "请不要输入空字符");

        // 判断用户名长度是否小于7位或大于16位
        ThrowUtils.throwIf(userAccount.length() < 7 || userAccount.length() > 16, StatusCode.PARAM_ERROR, "用户名长度在7到16位之间");

        // 密码长度是否小于8位或大于16位
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 16, StatusCode.PARAM_ERROR, "密码长度在8到16位之间");

        // 密码和确认密码是否一致
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), StatusCode.PARAM_ERROR, "两次输入的密码不一致");

        // 判断用户账号是否重复
        ThrowUtils.throwIf(this.query().eq("userAccount", userAccount).count() > 0, StatusCode.PARAM_ERROR, "用户名已存在");

        // 密码md5加密
        userPassword = md5(userPassword);

        // 插入数据到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        user.setUserRole(UserRole.USER.getValue());
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, StatusCode.SERVER_ERROR, "用户注册失败，数据库错误");
        return user.getId();
    }

    // 公共md5加密方法
    private String md5(String originString) {
        // 加盐为 fish
        return DigestUtils.md5DigestAsHex((originString+"fish").getBytes());
    }
}




