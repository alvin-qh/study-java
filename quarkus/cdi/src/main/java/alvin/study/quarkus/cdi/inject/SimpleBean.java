package alvin.study.quarkus.cdi.inject;

import jakarta.inject.Singleton;
import lombok.Getter;

/**
 * 用于演示依赖注入的简单 Bean
 *
 * <p>
 * Quarkus 中 Bean 的作用域分为两大类: "常规作用域"和"伪作用域", 前者真实的定义了一个 Bean 的生命周期和使用范围; 后者则只是定义了 Bean
 * 的生命周期, 其作用域是全局的
 * </p>
 *
 * <p>
 * 常规作用域包括: {@link jakarta.enterprise.context.ApplicationScoped @ApplicationScoped},
 * {@link jakarta.enterprise.context.RequestScoped @RequestScoped} 以及
 * {@link jakarta.enterprise.context.SessionScoped @SessionScoped}, 其作用域分别为: 全局范围, 一次请求范围和一次会话范围
 * </p>
 *
 * <p>
 * 伪作用域包括: {@link Singleton @Singleton} 和 {@link jakarta.enterprise.context.Dependent @Dependent}, 前者表示 Bean
 * 是单例的 (全局唯一), 且会伴随 Quarkus 容器初始化时创建; 后者表示每次注入时都会创建一个新的 Bean 对象
 * </p>
 *
 * <p>
 * 如无特殊原因, 应该使用 {@link Singleton @Singleton} 注解, 以达到最好的效率
 * </p>
 */
@Getter
@Singleton
public class SimpleBean {
    private final String name;

    /**
     * 默认构造器
     *
     * <p>
     * 本例中必须提供默认构造器, 否则 {@link Singleton @Singleton} 注解将无法实例化当前类型实例
     * </p>
     */
    public SimpleBean() {
        this("Default");
    }

    /**
     * 参数构造器, 设置 {@code name} 属性
     *
     * <p>
     * 该构造器用于构造特殊对象, 参见 {@link SimpleBeanProduces} 类型中的使用方法
     * </p>
     *
     * @param name {@code name} 属性
     */
    public SimpleBean(String name) {
        this.name = name;
    }
}
