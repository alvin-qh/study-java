package alvin.study.app.endpoint.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录表单类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LoginForm implements Serializable {
    // 用户名
    @NotBlank
    private String username;

    // 密码
    @NotBlank
    private String password;
}
