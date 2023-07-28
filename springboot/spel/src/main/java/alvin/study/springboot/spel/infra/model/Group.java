package alvin.study.springboot.spel.infra.model;

import com.google.common.base.Functions;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用于测试 SpEL 获取对象属性值的 Pojo 类型
 */
@Data
@Builder
public class Group {
    private final String name;
    private final User[] users;

    /**
     * {@code usersAsList} 属性, 将 {@code users} 属性以 {@link List} 集合返回
     *
     * @return 包含 {@link User} 对象的 {@link List} 集合
     */
    public List<User> getUsersAsList() {
        return List.of(users);
    }

    /**
     * {@code usersAsMap} 属性, 将 {@code users} 属性以 {@link Map} 集合返回
     *
     * <p>
     * 其中, Key 为 {@link User#getName()} 属性值, Value 为 {@link User} 对象值
     * </p>
     *
     * @return 包含 {@link User} 对象的 {@link Map} 集合
     */
    public Map<String, User> getUsersAsMap() {
        return Arrays.stream(users)
            .collect(Collectors.toMap(User::getName, Functions.identity()));
    }
}
