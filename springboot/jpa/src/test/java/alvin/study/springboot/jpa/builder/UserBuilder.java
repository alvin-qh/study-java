package alvin.study.springboot.jpa.builder;

import alvin.study.springboot.jpa.infra.entity.User;
import alvin.study.springboot.jpa.util.security.PasswordUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户实体构建器类
 */
public class UserBuilder extends Builder<User> {
    @Autowired
    private PasswordUtil passwordUtil;

    private String account = FAKER.name().username();
    private String password = "c926d53ca183e8bb5a369e8752b4ed574304bf1f15d680b8304f3251306915ec";

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

    @Override
    public User build() {
        var user = new User();
        user.setAccount(account);
        user.setPassword(password);
        return fillOrgId(user);
    }
}
