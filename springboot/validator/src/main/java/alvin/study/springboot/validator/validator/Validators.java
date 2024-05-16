package alvin.study.springboot.validator.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 以编程方式对指定对象进行校验
 *
 * <p>
 * Spring 框架中包含两种 {@link Validator} 接口实现类, 分别由 Spring 和 Hibernate 提供,
 * 两种验证器的行为基本一致, 但获取的渠道不同
 * </p>
 *
 * <p>
 * 对于 Spring 提供的 {@link Validator} 实现类型
 * {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
 * LocalValidatorFactoryBean}, 是通过 Spring 注入方式获得
 * </p>
 *
 * <p>
 * 对于 Hibernate 提供的 {@link Validator} 实现类型
 * {@link org.hibernate.validator.internal.engine.ValidatorImpl ValidatorImpl},
 * 是通过 {@link jakarta.validation.ValidatorFactory ValidatorFactory} 工厂来创建
 * </p>
 */
@Component
@RequiredArgsConstructor
public class Validators {
    /**
     * 从 Spring bean 容器中注入 {@link Validator} 接口对象, 实际注入对象为
     * {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
     * LocalValidatorFactoryBean} 类型对象
     */
    private final Validator validator;

    /**
     * 执行校验
     *
     * <p>
     * 对 {@code obj} 参数进行校验. 该方法会对 {@code obj} 对象的所有字段上设置的验证注解进行验证, 返回所有验证失败的结果集合
     * </p>
     *
     * <p>
     * {@code groups} 参数可以定义一组类型, 若验证器注解 {@code group()} 属性与之对应, 则该验证器注解对应的验证器会被执行
     * </p>
     *
     * @param <T>    待校验对象的类型
     * @param obj    待校验对象
     * @param groups 要执行的验证规则注解分组
     * @return 校验结果集合, 每个校验规则若未通过均会产生一个校验结果
     */
    public <T> Set<ConstraintViolation<T>> validate(T obj, Class<?>... groups) {
        return validator.validate(obj, groups);
    }

    /**
     * 获取通过注入方式获得的校验对象, 该验证对象由 Spring 框架提供
     *
     * <p>
     * 参考 {@link #validator}
     * </p>
     *
     * @return {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
     * LocalValidatorFactoryBean} 类型对象
     */
    public Validator getInjectedValidator() {
        return validator;
    }

    /**
     * 获取提供工厂方法产生的校验对象, 该验证对象由 Hibernate 框架提供
     *
     * <p>
     * 参考 {@link jakarta.validation.ValidatorFactory#getValidator()
     * ValidatorFactory.getValidator()}
     * </P>
     *
     * @return {@link org.hibernate.validator.internal.engine.ValidatorImpl
     * ValidatorImpl} 类型对象
     */
    public Validator getDefaultValidator() {
        // 获取默认的验证对象工厂
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            // 获取验证对象
            return factory.getValidator();
        }
    }
}
