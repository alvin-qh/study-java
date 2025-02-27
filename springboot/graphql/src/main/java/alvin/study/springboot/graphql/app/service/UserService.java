package alvin.study.springboot.graphql.app.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.graphql.core.exception.InputException;
import alvin.study.springboot.graphql.core.exception.InternalException;
import alvin.study.springboot.graphql.core.exception.UnauthorizedException;
import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.mapper.UserMapper;
import alvin.study.springboot.graphql.util.security.Jwt;
import alvin.study.springboot.graphql.util.security.PasswordUtil;

/**
 * 用户服务类, 用于 {@link User} 类型数据操作
 */
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    private final Jwt jwt;
    private final PasswordUtil passwordUtil;

    /**
     * 根据用户 {@code ID} 值查询 {@link User} 类型用户实体对象
     *
     * @param id 用户实体 {@code ID}
     * @return {@link User} 类型用户实体对象的 {@link Optional} 包装对象
     */
    @Transactional(readOnly = true)
    public User findById(long orgId, long id) {
        return Optional.ofNullable(
            userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                    .eq(User::getOrgId, orgId)
                    .eq(User::getId, id)))
                .orElseThrow(() -> new InputException("user_not_exist"));
    }

    /**
     * 将用户密码字段进行加密
     *
     * @param user {@link User} 类型实体对象
     * @return {@link User} 类型实体对象
     */
    private User encryptUserPassword(User user) {
        try {
            user.setPassword(passwordUtil.encrypt(user.getPassword()));
            return user;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InternalException(e);
        }
    }

    /**
     * 创建 {@link User} 类型用户实体对象
     *
     * @param user {@link User} 类型用户实体对象
     */
    @Transactional
    public void create(User user) {
        userMapper.insert(encryptUserPassword(user));
    }

    /**
     * 更新 {@link User} 类型用户实体对象
     *
     * @param id    用户实体 {@code ID}
     * @param orgId 组织 {@code ID}
     * @param user  {@link User} 用户实体对象的 {@link Optional} 包装对象
     * @return {@link User} 类型用户实体对象的 {@link Optional} 包装对象
     */
    @Transactional
    public void update(User user) {
        var originalUser = findById(user.getOrgId(), user.getId());
        user.setPassword(originalUser.getPassword());

        if (userMapper.update(user, Wrappers.lambdaUpdate(User.class)
                .eq(User::getId, user.getId())
                .eq(User::getOrgId, user.getOrgId()))
            == 0) {
            throw new InputException("user_not_exist");
        }
    }

    /**
     * 删除 {@link User} 类型用户实体
     *
     * @param orgId 组织实体 {@code ID} 值
     * @param id    用户实体 {@code ID} 值
     * @return {@code true} 表示删除成功, {@code false} 表示删除失败
     */
    @Transactional
    public boolean delete(long orgId, long id) {
        return userMapper.update(Wrappers.lambdaUpdate(User.class)
                .set(User::getDeleted, 1)
                .eq(User::getId, id)
                .eq(User::getOrgId, orgId))
               > 0;
    }

    @Transactional(readOnly = true)
    public String login(long orgId, String account, String password) {
        var user = userMapper.selectOne(
            Wrappers.lambdaQuery(User.class)
                    .eq(User::getOrgId, orgId)
                    .eq(User::getAccount, account));

        if (user == null) {
            throw new UnauthorizedException("user_not_exist");
        }
        try {
            if (!passwordUtil.verify(password, user.getPassword())) {
                throw new UnauthorizedException("invalid_password");
            }
            return jwt.encode(user.getOrgId().toString(), user.getId().toString());
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InternalError(e);
        }
    }
}
