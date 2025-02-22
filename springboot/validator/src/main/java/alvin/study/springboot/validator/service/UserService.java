package alvin.study.springboot.validator.service;

import java.util.Random;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import alvin.study.springboot.validator.model.User;

/**
 * 定义类型进行参数校验
 *
 * <p>
 * 可以通过 {@link Validated#value()} 属性指定校验的 {@code groups}, 以便在不同时候应用不同的校验规则, 例如:
 * </p>
 *
 * <pre>
 * &#64;Validated({ Group1.class, Group2.class })
 * </pre>
 *
 * <p>
 * 要使验证器生效, 当前类的对象必须经由 Bean 容器管理, 即需要为当前类加上
 * {@link Service @Service},
 * {@link org.springframework.stereotype.Component @Component} 等注解
 * </p>
 *
 * <p>
 * 要对简单参数进行校验, 需要为当前类加上 {@link Validated @Validated} 注解
 * </p>
 *
 * <p>
 * 对于复杂参数进行校验, 除需要为当前类增加 {@link Validated @Validated} 注解外, 还需要为参数加上
 * {@link Valid @Valid} 注解
 * </p>
 */
@Service
@Validated
public class UserService {
    private static final Random RANDOM = new Random();

    /**
     * 对基本参数进行校验
     *
     * @param name 进行非空校验
     * @param age  进行非 {@code null} 校验和数值范围校验
     * @return 校验成功, 产生实体类
     */
    public User createUser(
            @NotBlank String name,
            @NotNull @Min(10) @Max(100) Integer age) {
        return new User((long) RANDOM.nextInt(1000), name, age);
    }

    /**
     * 对复合类型的参数进行校验
     *
     * <p>
     * 对于 {@link User} 类型参数, 其类型字段也包含了校验注释, 所以需要增加 {@link Valid @Valid} 注解
     * </p>
     *
     * @param user {@link User} 类型参数, 字段带有校验规则
     */
    public void updateUser(@Valid User user) { /* Keep blank */ }
}
