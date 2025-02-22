package alvin.study.springboot.graphql.app.api.query;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.infra.entity.User;

/**
 * 用户查询类
 *
 * <p>
 * 对应 {@code classpath:graphql/user.graphqls} 中的定义
 * </p>
 */
@Controller
@RequiredArgsConstructor
public class UserQuery {
    // 注入服务对象
    private final UserService userService;

    /**
     * 用户查询
     *
     * @param id 用户 id
     * @return 用户对象
     */
    @QueryMapping
    public User user(@Argument String id) {
        // 查询用户
        return userService.findById(Long.parseLong(id))
                .orElseThrow(() -> new InputException("Invalid user id"));
    }
}
