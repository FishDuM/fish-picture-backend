package hk.fish.fishpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.domain.dto.user.UserAddRequest;
import hk.fish.fishpicturebackend.domain.dto.user.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.user.UserQueryRequest;
import hk.fish.fishpicturebackend.domain.dto.user.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRole;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;
import hk.fish.fishpicturebackend.domain.vo.UserVO;
import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.StatusCode;
import hk.fish.fishpicturebackend.exception.ThrowUtils;
import hk.fish.fishpicturebackend.service.UserService;
import hk.fish.fishpicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hk.fish.fishpicturebackend.common.BaseCode.*;

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
     * 用户登出
     * @param request 请求
     */
    @Override
    public void userLogout(HttpServletRequest request) {
        // 获取当前用户
        User user = this.getCurrentUserInSystem(request);
        ThrowUtils.throwIf(user == null, StatusCode.UN_LOGIN_ERROR, "操作失败未登录");
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
    }

    /**
     * 获取用户信息脱敏
     * @param user 用户
     * @return 用户信息脱敏
     */
    @Override
    public UserVO getUserVO(User user) {
        return Bean2OtherBean(user, UserVO.class);
    }

    /**
     * 获取用户信息脱敏列表
     * @param userList 用户列表
     * @return 用户信息脱敏列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (userList == null){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * Bean转换
     * @param originBean 源Bean
     * @param targetBean 目标Bean
     * @return 目标Bean
     */
    public  <T, E> T  Bean2OtherBean(E originBean, Class<T> targetBean) {
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
    public String md5(String originString) {
        // 加盐为 fish
        return DigestUtils.md5DigestAsHex((originString+"fish").getBytes());
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询请求类
     * @return 查询条件
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAM_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 管理员添加用户
     *
     * @param userAddRequest 用户添加请求类
     * @return 添加结果
     */
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        // 获取参数
        String userName = userAddRequest.getUserName();
        String userAccount = userAddRequest.getUserAccount();
        String userRole = userAddRequest.getUserRole();

        // 校验参数是否为空
        ThrowUtils.throwIf(StrUtil.isBlank(userName) || StrUtil.isBlank(userAccount), StatusCode.PARAM_ERROR, "参数不能为空");
        // 校验参数长度
        ThrowUtils.throwIf(userName.length() > 16 || userAccount.length() < 7 || userAccount.length() > 16, StatusCode.PARAM_ERROR, "参数长度错误");

        // 转换为User
        User user = Bean2OtherBean(userAddRequest, User.class);

        // 判断角色是否为空
        if (StrUtil.isBlank(userRole)){
            // 为空自动添加用户角色
            user.setUserRole(USER);
        }
        // 设置默认密码
        user.setUserPassword(md5(BASE_PASSWORD));
        // 查询账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        ThrowUtils.throwIf(this.count(queryWrapper) > 0, StatusCode.PARAM_ERROR, "账号已存在");

        // 插入数据
        boolean save = this.save(user);
        ThrowUtils.throwIf(!save, StatusCode.SERVER_ERROR, "添加失败");
        return user.getId();
    }

}




