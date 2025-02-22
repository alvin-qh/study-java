package alvin.study.springboot.validator.validator;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.validator.IntegrationTest;
import alvin.study.springboot.validator.model.Computer;
import alvin.study.springboot.validator.model.User;

/**
 * 测试 {@link Validators} 工具类, 对具备校验注解字段的对象进行校验
 *
 * <p>
 * 主要用于以编程方式 (而非注解方式) 对注解有校验规则的类对象进行校验
 * </p>
 */
class ValidatorsTest extends IntegrationTest {
    /**
     * 注入校验对象
     */
    @Autowired
    private Validators validators;

    /**
     * 测试不同方法获取的验证器对象
     */
    @Test
    void getInjectedValidator_getDefaultValidator_shouldDifferentInstance() {
        // 确认不同方式获取的 Validator 对象不同
        then(validators.getInjectedValidator()).isNotSameAs(validators.getDefaultValidator());
    }

    /**
     * 测试以注入方式获取的 {@link jakarta.validation.Validator Validator} 对象
     */
    @Test
    void validate_shouldSpringValidatorWorked() {
        // 构造一个字段不符合验证规则的对象
        var user = new User(null, "", 5);

        // 执行验证, 确认验证结果失败
        var violations = validators.validate(user);
        then(violations).hasSize(3);

        // 确认验证失败结果对应的信息
        for (var violation : violations) {
            switch (violation.getPropertyPath().toString()) {
            case "id" -> then(violation.getMessage()).isEqualTo("must not be null");
            case "name" -> then(violation.getMessage()).isEqualTo("must not be blank");
            case "age" -> then(violation.getMessage()).isEqualTo("must be greater than or equal to 10");
            default -> fail();
            }
        }
    }

    /**
     * 测试以工厂方法获取的 {@link jakarta.validation.Validator Validator} 对象
     */
    @Test
    void validate_shouldHibernateValidatorWorked() {
        // 构造一个字段不符合验证规则的对象
        var computer = new Computer("", "100.200");

        // 执行验证, 确认验证结果失败
        var violations = validators.validate(computer);
        then(violations).hasSize(2);

        // 确认验证失败结果对应的信息
        for (var violation : violations) {
            switch (violation.getPropertyPath().toString()) {
            case "name" -> then(violation.getMessage()).isEqualTo("must not be blank");
            case "ipAddress" -> then(violation.getMessage()).isEqualTo("invalid IP address");
            default -> fail();
            }
        }
    }
}
