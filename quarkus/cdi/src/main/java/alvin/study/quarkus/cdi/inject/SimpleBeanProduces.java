package alvin.study.quarkus.cdi.inject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import alvin.study.quarkus.cdi.inject.anno.A;
import alvin.study.quarkus.cdi.inject.anno.B;

/**
 * 用于通过方法提供 Bean 对象
 */
public class SimpleBeanProduces {
    /**
     * 通过注解 {@link A @A} 标识容器对象
     *
     * <p>
     * 所有通过 {@link A @A} 注解进行注入的位置, 都将注入该方法返回的对象
     * </p>
     *
     * <p>
     * {@link ApplicationScoped @ApplicationScoped} 注解表示, 该方法返回的 {@link SimpleBean} 类型对象在整个应用程序中是唯一的
     * </p>
     *
     * @return {@link SimpleBean} 类型对象
     */
    @A
    @Produces
    @ApplicationScoped
    SimpleBean simpleServiceA() {
        return new SimpleBean("A");
    }

    /**
     * 通过注解 {@link B @B} 标识容器对象
     *
     * <p>
     * 所有通过 {@link B @B} 注解进行注入的位置, 都将注入该方法返回的对象
     * </p>
     *
     * <p>
     * {@link Dependent @Dependent} 注解表示, 每次注入 {@link SimpleBean} 时都会产生新的对象
     * </p>
     *
     * @return {@link SimpleBean} 类型对象
     */
    @B
    @Produces
    @Dependent
    SimpleBean simpleServiceB() {
        return new SimpleBean("B");
    }
}
