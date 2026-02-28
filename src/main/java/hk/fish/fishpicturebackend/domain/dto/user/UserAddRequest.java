package hk.fish.fishpicturebackend.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 给管理员用的用户创建请求类
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
