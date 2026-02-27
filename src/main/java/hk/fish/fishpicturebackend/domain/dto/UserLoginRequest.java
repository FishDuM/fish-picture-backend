package hk.fish.fishpicturebackend.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    /**
     * 序列化id
     */
    private static final long serialVersionUID = -8859405245461880218L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
