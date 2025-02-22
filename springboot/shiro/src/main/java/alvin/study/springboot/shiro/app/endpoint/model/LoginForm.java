package alvin.study.springboot.shiro.app.endpoint.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录表单类
 */
@Data
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class LoginForm implements Serializable {
    /**
     * 用户账号
     */
    @NotBlank
    @Length(min = 1, max = 20)
    private String account;

    /**
     * 用户密码
     */
    @NotBlank
    @Length(min = 6, max = 30)
    private String password;
}
