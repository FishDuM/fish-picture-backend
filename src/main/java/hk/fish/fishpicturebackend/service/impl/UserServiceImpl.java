package hk.fish.fishpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.dto.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRole;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.service.UserService;
import hk.fish.fishpicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static hk.fish.fishpicturebackend.common.BaseCode.USER_LOGIN_STATUS;

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
        ThrowUtils.throwIf(this.query().eq("user_account", userAccount).count() > 0, StatusCode.PARAM_ERROR, "用户名已存在");

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

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求类
     * @return 用户信息
     */
    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 提取参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 判断数据是否为空
        ThrowUtils.throwIf((userAccount == null || userPassword == null),StatusCode.PARAM_ERROR, "请不要输入空字符");

        // 判断用户名长度是否小于7位或大于16位
        ThrowUtils.throwIf(userAccount.length() < 7 || userAccount.length() > 16, StatusCode.PARAM_ERROR, "用户名长度在7到16位之间");

         // 密码长度是否小于8位或大于16位
        ThrowUtils.throwIf(userPassword.length() < 8 || userPassword.length() > 16, StatusCode.PARAM_ERROR, "密码长度在8到16位之间");

        // 密码md5加密
        userPassword = md5(userPassword);

        // 查询用户
        User user = this.query().eq("user_account", userAccount).eq("user_password", userPassword).one();

        // 判断用户是否存在
        ThrowUtils.throwIf(user == null || user.getIsDelete() == null, StatusCode.UN_LOGIN_ERROR, "用户不存在或密码错误");

        // 返回用户信息
        request.getSession().setAttribute(USER_LOGIN_STATUS, user);
        return  this.Bean2OtherBean(user , LoginUserVO.class);

    }

    /**
     * 获取当前登录用户(前端，用户信息脱敏)
     * @param request 请求
     * @return 当前登录用户
     */
    @Override
    public LoginUserVO getCurrentUser(HttpServletRequest request) {
        User user = this.getCurrentUserInSystem(request);
        return this.Bean2OtherBean(user, LoginUserVO.class);
    }

    /**
     * 获取当前用户信息(系统，用户信息未脱敏)
     * @param request 请求
     * @return 当前用户信息
     */
    @Override
    public User getCurrentUserInSystem(HttpServletRequest request) {
        // 判断是否已登录
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        ThrowUtils.throwIf(user == null || user.getId() == null, StatusCode.UN_LOGIN_ERROR, "未登录");
        // 防止不一致，从数据库再获取一遍
        user = this.getById(user.getId());
        // 防止被删除，但还能登录
        ThrowUtils.throwIf(user == null, StatusCode.UN_LOGIN_ERROR, "用户不存在");
        return user;
    }

    /**
     * Bean转换
     * @param originBean 源Bean
     * @param targetBean 目标Bean
     * @return 目标Bean
     */
    private  <T, E> T  Bean2OtherBean(E originBean, Class<T> targetBean) {
        if (originBean == null || targetBean == null) {
            return null;
        }
        return BeanUtil.copyProperties(originBean, targetBean);
    }

    /**
     * md5加密
     * @param originString 原始字符串
     * @return 加密后的字符串
     */
    private String md5(String originString) {
        // 加盐为 fish
        return DigestUtils.md5DigestAsHex((originString+"fish").getBytes());
    }
}




