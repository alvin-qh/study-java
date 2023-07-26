package alvin.study.springboot.kickstart.app.api.schema.type;

/**
 * 用户分组枚举
 *
 * <p>
 * 参考 {@code classpath:graphql/user.graphqls} 中定义查询的 schema
 * </p>
 */
public enum UserGroup {
    ADMIN,
    OPERATOR,
    NORMAL
}
