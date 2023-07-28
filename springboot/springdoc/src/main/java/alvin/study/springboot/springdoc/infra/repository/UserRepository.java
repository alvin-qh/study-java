package alvin.study.springboot.springdoc.infra.repository;

import alvin.study.springboot.springdoc.infra.entity.User;
import alvin.study.springboot.springdoc.infra.repository.common.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户实体对象持久化类型
 *
 * <p>
 * 这里的用户实体表示 {@link User} 对象, 即 API 访问用户
 * </p>
 */
@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String NAME_ACCESS_USERS = "access-users";

    /**
     * 根据用户名查询用户实体
     *
     * @param username 用户名
     * @return 用户实体对象
     */
    public Optional<User> selectUserByName(String username) {
        var storage = getStorage(NAME_ACCESS_USERS);
        return storage.get(username);
    }
}
