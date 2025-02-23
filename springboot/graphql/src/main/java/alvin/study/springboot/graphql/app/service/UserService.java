package alvin.study.springboot.graphql.app.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.mapper.UserMapper;
import alvin.study.springboot.graphql.util.security.PasswordUtil;

/**
 * 用户服务类, 用于 {@link User} 类型数据操作
 */
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    /**
     * 根据用户 {@code ID} 值查询 {@link User} 类型用户实体对象
     *
     * @param id 用户实体 {@code ID}
     * @return {@link User} 类型用户实体对象的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    /**
     * 创建 {@link User} 类型用户实体对象
     *
     * @param user {@link User} 类型用户实体对象
     */
    @Transactional
    public void create(User user) {
        try {
            user.setPassword(passwordUtil.encrypt(user.getPassword()));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InternalError("Cannot encrypt password", e);
        }
        userMapper.insert(user);
    }

    /**
     * 更新 {@link User} 类型用户实体对象
     *
     * @param id   用户实体 {@code ID}
     * @param user {@link User} 用户实体对象的 {@link Optional} 包装对象
     * @return {@link User} 类型用户实体对象的 {@link Optional} 包装对象
     */
    @Transactional
    public Optional<User> update(long id, User user) {
        var originalUser = userMapper.selectById(id);
        if (originalUser == null) {
            return Optional.empty();
        }

        try {
            originalUser.setPassword(passwordUtil.encrypt(user.getPassword()));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InternalError("Cannot encrypt password", e);
        }
        originalUser.setAccount(user.getAccount());

        if (userMapper.updateById(originalUser) > 0) {
            return Optional.of(originalUser);
        }
        return Optional.empty();
    }

    /**
     * 删除 {@link User} 类型用户实体
     *
     * @param id 用户实体 {@code ID} 值
     * @return {@code true} 表示删除成功, {@code false} 表示删除失败
     */
    @Transactional
    public boolean delete(long id) {
        return userMapper.deleteById(id) > 0;
    }
}
