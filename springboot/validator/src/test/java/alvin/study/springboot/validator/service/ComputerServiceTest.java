package alvin.study.springboot.validator.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import alvin.study.springboot.validator.IntegrationTest;
import alvin.study.springboot.validator.model.Computer;

/**
 * 测试对具备验证注解的参数进行校验
 *
 * <p>
 * {@code Computer.ipAddress} 的字段具备自定义
 * {@link alvin.study.validator.IpAddress IpAddress} 校验注解
 * </p>
 */
class ComputerServiceTest extends IntegrationTest {
    /**
     * 注入具备 {@link org.springframework.validation.annotation.Validated @Validated} 注解的服务类对象
     */
    @Autowired
    private ComputerService computerService;

    /**
     * 测试参数正确时的情况
     *
     * <p>
     * 调用 {@link ComputerService#createComputer(String, String)} 方法并传入正确参数, 返回正确的 {@link Computer} 对象
     * </p>
     *
     * <p>
     * 调用 {@link ComputerService#updateComputer(Computer)} 方法并传入正确的 {@link Computer} 对象, 方法正确执行
     * </p>
     */
    @Test
    void createComputer_updateComputer_shouldArgumentsValidSuccess() {
        // 传入简单参数, 参数正确, 执行通过
        var computer = computerService.createComputer("Lenovo H300", "192.168.1.111");

        then(computer.getName()).isEqualTo("Lenovo H300");
        then(computer.getIpAddress()).isEqualTo("192.168.1.111");

        // 传入复合对象参数, 参数正确, 执行通过
        computerService.updateComputer(computer);
    }

    /**
     * 测试简单参数错误时的情况
     *
     * <p>
     * 调用 {@link ComputerService#createComputer(String, String)} 方法并传入错误参数, 抛出
     * {@link ConstraintViolationException} 异常
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
     * {@link jakarta.validation.ConstraintViolation#getMessage() ConstraintViolation.getMessage()}
     * 方法返回校验错误的错误信息
     * </li>
     * </ul>
     * </p>
     */
    @Test
    void createComputer_shouldArgumentsValidFailed() {
        try {
            // 传入简单参数, 参数无效, 执行失败抛出异常
            computerService.createComputer("Lenovo H300", "100.100.1");
        } catch (ConstraintViolationException e) {
            // 共有 1 个参数验证失败
            then(e.getConstraintViolations()).hasSize(1);

            // 获取第 1 个校验结果
            var violation = e.getConstraintViolations().iterator().next();

            // 获取校验失败的参数路径, 格式为: 方法名.参数名
            then(violation.getPropertyPath()).hasToString("createComputer.ipAddress");

            // 获取校验失败的错误信息
            then(violation.getMessage()).isEqualTo("invalid IP address");
        }
    }

    /**
     * 测试复合对象参数错误时的情况
     *
     * <p>
     * 调用 {@link ComputerService#updateComputer(Computer)} 方法并传入错误对象参数, 抛出
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
    void updateComputer_shouldArgumentsValidFailed() {
        try {
            // 传入复合对象参数, 参数无效, 执行失败抛出异常
            computerService.updateComputer(new Computer("", "100.200"));
        } catch (ConstraintViolationException e) {
            // 共有 2 个参数验证失败
            then(e.getConstraintViolations()).hasSize(2);

            // 遍历错误集合
            for (var violation : e.getConstraintViolations()) {
                // 根据校验失败参数路径, 确认相关的错误信息
                // 对于复合参数类型, 路径格式为: 方法名.参数名.字段名
                switch (violation.getPropertyPath().toString()) {
                case "updateComputer.computer.name" -> then(violation.getMessage()).isEqualTo("must not be blank");
                case "updateComputer.computer.ipAddress" -> then(violation.getMessage())
                        .isEqualTo("invalid IP address");
                default -> fail();
                }
            }
        }
    }
}
