package hk.fish.fishpicturebackend.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求封装类
 */
@Data
public class UserRegisterRequest implements Serializable {
    /**
     * 序列化id
     */
    private static final long serialVersionUID = 3095128210558961794L;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;

    /**
     * 再次确认密码
     */
    private String checkPassword;

}
