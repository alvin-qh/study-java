package alvin.study.springboot.validator.service;

import alvin.study.springboot.validator.IntegrationTest;
import alvin.study.springboot.validator.model.User;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 测试对具备验证注解的参数进行校验
 *
 * <p>
 * {@link User} 的 {@code id, name, age} 字段具备校验注解
 * </p>
 */
class UserServiceTest extends IntegrationTest {
    /**
     * 注入具备 {@link org.springframework.validation.annotation.Validated @Validated}
     * 注解的服务类对象
     */
    @Autowired
    private UserService userService;

    /**
     * 测试参数正确时的情况
     *
     * <p>
     * 调用 {@link UserService#createUser(String, Integer)} 方法并传入正确参数, 返回正确的 {@link User} 对象
     * </p>
     *
     * <p>
     * 调用 {@link UserService#updateUser(User)} 方法并传入正确的 {@link User} 对象, 方法正确执行
     * </p>
     */
    @Test
    void createUser_updateUser_shouldArgumentsValidSuccess() {
        // 传入简单参数, 参数正确, 执行通过
        var user = userService.createUser("Alvin", 30);

        then(user.getId()).isNotNull();
        then(user.getName()).isEqualTo("Alvin");
        then(user.getAge()).isEqualTo(30);

        // 传入复合对象参数, 参数正确, 执行通过
        userService.updateUser(user);
    }

    /**
     * 测试简单参数错误时的情况
     *
     * <p>
     * 调用 {@link UserService#createUser(String, Integer)} 方法并传入错误参数, 抛出 {@link ConstraintViolationException} 异常
     * </p>
     *
     * <p>
     * 通过 {@link ConstraintViolationException#getConstraintViolations()} 方法可以获取
     * {@link jakarta.validation.ConstraintViolation ConstraintViolation} 对象, 表示详细的验证错误信息, 其中:
     * <ul>
     * <li>
     * {@link jakarta.validation.ConstraintViolation#getPropertyPath() ConstraintViolation.getPropertyPath()}
     * 方法返回校验错误的 {@code 参数/属性} 的路径, 对于简单参数, 格式为 {@code 方法名.参数名}
     * </li>
     * <li>
     * {@link jakarta.validation.ConstraintViolation#getMessage() ConstraintViolation.getMessage()} 方法返回校验错误的错误信息
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void createUser_shouldArgumentsValidFailed() {
        try {
            // 传入简单参数, 参数无效, 执行失败抛出异常
            userService.createUser("", 30);
        } catch (ConstraintViolationException e) {
            // 共有一个参数验证失败
            then(e.getConstraintViolations()).hasSize(1);

            // 获取第一个校验结果
            var violation = e.getConstraintViolations().iterator().next();

            // 获取校验失败的参数路径, 格式为: 方法名.参数名
            then(violation.getPropertyPath()).hasToString("createUser.name");

            // 获取校验失败的错误信息
            then(violation.getMessage()).isEqualTo("must not be blank");
        }
    }

    /**
     * 测试复合对象参数错误时的情况
     *
     * <p>
     * 调用 {@link UserService#updateUser(User)} 方法并传入错误对象参数, 抛出
     * {@link ConstraintViolationException} 异常
     * </p>
     *
     * <p>
     * 通过 {@link ConstraintViolationException#getConstraintViolations()} 方法可以获取
     * {@link jakarta.validation.ConstraintViolation ConstraintViolation} 对象, 表示详细的验证错误信息, 其中:
     * <ul>
     * <li>
     * {@link jakarta.validation.ConstraintViolation#getPropertyPath() ConstraintViolation.getPropertyPath()}
     * 方法返回校验错误的 {@code 参数/属性} 的路径, 对于复杂类型参数, 格式为 {@code 方法名.参数名.字段名}
     * </li>
     * <li>
     * {@link jakarta.validation.ConstraintViolation#getMessage() ConstraintViolation.getMessage()}
     * 方法返回校验错误的错误信息
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void updateUser_shouldArgumentsValidFailed() {
        try {
            // 传入复合对象参数, 参数无效, 执行失败抛出异常
            userService.updateUser(new User(null, "", 5));
        } catch (ConstraintViolationException e) {
            // 共有 3 个参数验证失败
            then(e.getConstraintViolations()).hasSize(3);

            // 遍历错误集合
            for (var violation : e.getConstraintViolations()) {
                // 根据校验失败参数路径, 确认相关的错误信息
                // 对于复合参数类型, 路径格式为: 方法名.参数名.字段名
                switch (violation.getPropertyPath().toString()) {
                case "updateUser.user.id" -> then(violation.getMessage()).isEqualTo("must not be null");
                case "updateUser.user.name" -> then(violation.getMessage()).isEqualTo("must not be blank");
                case "updateUser.user.age" ->
                    then(violation.getMessage()).isEqualTo("must be greater than or equal to 10");
                default -> fail();
                }
            }
        }
    }
}
