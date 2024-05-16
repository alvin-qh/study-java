package alvin.study.springboot.validator.service;

import alvin.study.springboot.validator.model.Computer;
import alvin.study.springboot.validator.validator.IpAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
@Validated // 指定需要对方法参数进行校验
public class ComputerService {
    /**
     * 对基本参数进行校验
     *
     * @param name      进行非空校验
     * @param ipAddress 进行非空校验和 ip 地址校验
     * @return 校验成功, 产生实体类
     */
    public Computer createComputer(
        @NotBlank String name,
        @NotBlank @IpAddress String ipAddress) {
        return new Computer(name, ipAddress);
    }

    /**
     * 对复合类型的参数进行校验
     *
     * <p>
     * 对于 {@link Computer} 类型参数, 其类型字段也包含了校验注释, 所以需要增加 {@link Valid @Valid} 注解
     * </p>
     *
     * @param computer {@link Computer} 类型参数, 字段带有校验规则
     */
    public void updateComputer(@Valid Computer computer) { /* Keep blank */ }
}
