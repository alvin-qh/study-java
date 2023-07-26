package alvin.study.springboot.kickstart.app.api.schema.input;

import alvin.study.springboot.kickstart.app.api.schema.type.UserGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 变更用户信息的输入类
 *
 * <p>
 * 对应的 schema 参考 {@code classpath:graphql/user.graphqls} 文件内容
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    /**
     * 用户帐号
     */
    @NotBlank
    @Length(min = 3, max = 50)
    private String account;

    /**
     * 用户密码
     */
    @NotBlank
    @Length(min = 6, max = 20)
    private String password;

    /**
     * 用户分组
     */
    @NotNull
    private UserGroup group;
}
