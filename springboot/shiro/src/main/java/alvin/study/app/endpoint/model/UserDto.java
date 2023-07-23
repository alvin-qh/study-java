package alvin.study.app.endpoint.model;

import java.io.Serializable;

import alvin.study.infra.entity.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录用户信息类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class UserDto implements Serializable {
    /**
     * 用户登录账号
     */
    private String account;

    /**
     * 用户类型
     */
    private UserType type;
}
