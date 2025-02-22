package alvin.study.springboot.validator.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 自定义校验注解, 对字段是否表示 IP 地址进行校验
 *
 * <p>
 * {@link Constraint @Constraint} 注解用于指定实际执行校验的类型, 参考 {@link IpAddressValidator}
 * 类型
 * </p>
 */
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { IpAddressValidator.class }) // 执行校验的校验类
public @interface IpAddress {
    /**
     * 设置 IP 版本号, 4 表示 ipv4, 6 表示 ipv6
     *
     * @return IP 地址的版本号
     */
    int version() default 4;

    /**
     * 校验失败的错误信息
     *
     * <p>
     * 该错误信息可以为一个字符串常量或者一个 i18n message key, 参见: {@code /i18n/message.properties}
     * 中的定义
     * </p>
     *
     * @return 错误信息或者错误信息的 Key
     */
    String message() default "{ip-address.invalid}";

    /**
     * 定义组, 组是一个任意类型, 一般为 {@code interface}, 用来约束该验证是否执行
     *
     * <p>
     * 默认情况下不设置组的校验器会被默认执行, 设置了组的校验器会在指定组的时候执行
     * </p>
     *
     * <p>
     * 执行校验时使用那个 {@code group} 由
     * {@link jakarta.validation.Validator#validate(Object, Class...)
     * Validator.validate(Object, Class...)} 方法从第二个开始的参数指定
     * </p>
     *
     * @return 用于对校验注解分组的类型标志
     */
    Class<?>[] groups() default {};

    /**
     * 一个作为载荷传递给校验结果的类型
     *
     * <p>
     * 可以传递 {@link Payload} 接口的实现类, 并且在校验失败后返回的结果
     * ({@link jakarta.validation.ConstraintViolation ConstraintViolation} 对象)
     * 中获取这个参数的值
     * </p>
     *
     * @return {@link Payload} 类型 {@link Class} 对象
     */
    Class<? extends Payload>[] payload() default {};
}
