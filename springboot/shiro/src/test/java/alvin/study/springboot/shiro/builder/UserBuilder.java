package alvin.study.springboot.shiro.builder;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import alvin.study.springboot.shiro.infra.entity.User;
import alvin.study.springboot.shiro.infra.entity.UserType;
import alvin.study.springboot.shiro.infra.mapper.UserMapper;
import alvin.study.springboot.shiro.util.security.PasswordEncoder;

/**
 * 用户实体构建器类
 */
public class UserBuilder implements Builder<User> {
    private final static AtomicInteger SEQUENCE = new AtomicInteger();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String account = "User" + SEQUENCE.incrementAndGet();
    private String password = "c926d53ca183e8bb5a369e8752b4ed574304bf1f15d680b8304f3251306915ec";
    private UserType type = UserType.NORMAL;

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
    public UserBuilder withPassword(String password) {
        this.password = passwordEncoder.encode(password);
        return this;
    }

    /**
     * 设置用户类型
     */
    public UserBuilder withType(UserType type) {
        this.type = type;
        return this;
    }

    @Override
    public User build() {
        var user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setType(type);
        return user;
    }

    @Override
    public User create() {
        var user = build();
        userMapper.insert(user);
        return user;
    }
}
