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
 * 用户服务类
 */
@Component
@RequiredArgsConstructor
public class UserService {
    /**
     * 注入 {@link UserMapper} 类型
     */
    private final UserMapper userMapper;

    /**
     * 密码处理工具对象
     */
    private final PasswordUtil passwordUtil;

    /**
     * 根据用户 id 名查询用户信息
     *
     * @param id 用户 id
     * @return 用户实体的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    /**
     * 创建一个 {@link User} 实体对象
     *
     * @param user {@link User} 对象
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
     * 更新一个 {@link User} 实体对象
     *
     * @param id   用户 id
     * @param user {@link User} 对象
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
     * 删除用户实体对象
     *
     * @param id 用户 ID
     * @return 是否删除用户
     */
    @Transactional
    public boolean delete(long id) {
        return userMapper.deleteById(id) > 0;
    }
}
