package alvin.study.springboot.kickstart.app.api.query;

import alvin.study.springboot.kickstart.app.api.common.BaseQuery;
import alvin.study.springboot.kickstart.app.api.schema.type.UserType;
import alvin.study.springboot.kickstart.app.service.UserService;
import alvin.study.springboot.kickstart.core.exception.InputException;
import alvin.study.springboot.kickstart.core.graphql.annotation.Query;
import lombok.RequiredArgsConstructor;

/**
 * 用户查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/user.graphqls} 中的定义
 * </p>
 *
 * <p>
 * Kickstart 框架定义的查询类需要包含如下几个条件:
 * <ul>
 * <li>
 * 实现 {@link graphql.kickstart.tools.GraphQLQueryResolver GraphQLQueryResolver}
 * 接口
 * </li>
 * <li>
 * 具备 {@link org.springframework.stereotype.Component @Component} 注解, 本例中用
 * {@link Query @Query} 注解替代
 * </li>
 * <li>
 * 在 {@code classpath:graphql/user.graphqls} 中定义查询的 Query schema
 * </li>
 * </ul>
 * </p>
 */
@Query
@RequiredArgsConstructor
public class UserQuery extends BaseQuery {
    // 注入服务对象
    private final UserService userService;

    /**
     * 用户查询
     *
     * @param id 用户 id
     * @return 用户对象
     */
    public UserType user(String id) {
        // 查询用户
        return userService.findById(Long.parseLong(id))
            .map(u -> map(u, UserType.class))
            .orElseThrow(() -> new InputException("Invalid user id"));
    }
}
