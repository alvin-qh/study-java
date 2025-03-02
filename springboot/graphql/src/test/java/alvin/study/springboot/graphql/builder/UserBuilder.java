package alvin.study.springboot.graphql.builder;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.SneakyThrows;

import alvin.study.springboot.graphql.infra.entity.User;
import alvin.study.springboot.graphql.infra.entity.UserGroup;
import alvin.study.springboot.graphql.infra.mapper.UserMapper;
import alvin.study.springboot.graphql.util.security.PasswordUtil;

/**
 * 用户实体构建器类
 */
public class UserBuilder extends Builder<User> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordUtil passwordUtil;

    private String account = "User" + SEQUENCE.incrementAndGet();
    private String password = "1050cf65d91df2644fc7c05a10efb67ab142aac70005ee62f2452f77d8d45827";
    private UserGroup group = UserGroup.ADMIN;

    /**
     * 设置账号
     */
    public UserBuilder withAccount(String account) {
        this.account = account;
        return this;
    }

    /**
     * 设置密码
     */
    @SneakyThrows
    public UserBuilder withPassword(String password) {
        this.password = passwordUtil.encrypt(password);
        return this;
    }

    /**
     * 设置用户类型
     */
    public UserBuilder withGroup(UserGroup group) {
        this.group = group;
        return this;
    }

    @Override
    public User build() {
        var user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setGroup(group);
        return complete(user);
    }

    @Override
    public User create() {
        var user = build();
        userMapper.insert(user);
        return user;
    }
}
