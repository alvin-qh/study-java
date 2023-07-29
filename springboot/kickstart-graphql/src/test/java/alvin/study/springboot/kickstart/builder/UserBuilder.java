package alvin.study.springboot.kickstart.builder;

import alvin.study.springboot.kickstart.infra.entity.User;
import alvin.study.springboot.kickstart.infra.entity.UserGroup;
import alvin.study.springboot.kickstart.infra.mapper.UserMapper;
import alvin.study.springboot.kickstart.util.security.PasswordUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

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
    private String password = "c926d53ca183e8bb5a369e8752b4ed574304bf1f15d680b8304f3251306915ec";
    private UserGroup group = UserGroup.NORMAL;

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
     * 设置用户分组
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
        return fillOrgId(user);
    }

    @Override
    public User create() {
        var user = build();
        userMapper.insert(user);
        return user;
    }
}
