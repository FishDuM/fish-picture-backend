package hk.fish.fishpicturebackend.domain.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    USER("用户", "user"),
    ADMIN("管理员", "admin");

    // 描述
    private final String text;
    // 用户角色
    private final String value;

    UserRole(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取到枚举类
     * @param value 用户角色
     * @return 该用户角色对应的枚举类
     */
    public static UserRole getUserRoleByValue(String value) {
        if(ObjectUtil.isEmpty(value)){
            return null;
        }
        for (UserRole userRole : UserRole.values()) {
            if (userRole.value.equals(value)){
                return userRole;
            }
        }
        return null;
    }
}
