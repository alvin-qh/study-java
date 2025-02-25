package alvin.study.springboot.graphql.app.model;

/**
 * 用户分组枚举类型
 *
 * <p>
 * 该类型用于映射 {@link alvin.study.springboot.graphql.infra.entity.UserGroup UserGroup} 类型,
 * 由于 {@link alvin.study.springboot.graphql.infra.entity.UserGroup UserGroup} 类型和其转换的字符串不匹配,
 * 故需要自定义一个枚举类型, 令枚举值和枚举转换为的字符串相匹配
 * </p>
 */
public enum UserGroup {
    ADMIN,
    OPERATOR,
    NORMAL;

    public static UserGroup of(alvin.study.springboot.graphql.infra.entity.UserGroup group) {
        switch (group) {
        case ADMIN:
            return ADMIN;
        case OPERATOR:
            return OPERATOR;
        case NORMAL:
            return NORMAL;
        default:
            throw new IllegalArgumentException("Unknown group");
        }
    }
}
