package alvin.study.springboot.kickstart.app.api.schema.type;

import alvin.study.springboot.kickstart.app.api.schema.type.common.AuditedType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户类型
 *
 * <p>
 * 参考 {@code classpath:graphql/user.graphqls} 中定义查询的 schema
 * </p>
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserType extends AuditedType {
    // 用户账号
    private String account;

    // 用户分组
    private UserGroup group;
}
