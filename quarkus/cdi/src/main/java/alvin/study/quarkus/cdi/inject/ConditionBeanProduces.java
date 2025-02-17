package alvin.study.quarkus.cdi.inject;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.arc.lookup.LookupUnlessProperty;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.enterprise.inject.Produces;

/**
 * 演示根据条件产生注入的 Bean 对象
 */
public class ConditionBeanProduces {
    /**
     * 根据配置项的属性值来确定是否产生对应的 Bean 对象
     *
     * <p>
     * {@link LookupIfProperty @LookupIfProperty} 注解表示当其 {@code name} 属性指定的配置项的值和 {@code stringValue}
     * 属性指定的值相等时, 会向 Quarkus 容器中放置该方法的返回值
     * </p>
     *
     * <p>
     * 参考 {@code resources:application.yml} 配置文件中的 {@code beans.condition.active} 配置项
     * </p>
     *
     * <p>
     * {@link LookupIfProperty @LookupIfProperty} 注解的 {@code lookupIfMissing} 属性表示, 如果指定的配置项缺失,
     * 是否实例化指定的 Bean
     * </p>
     *
     * @return {@link LookupIfPropertyBean} 类型对象
     */
    @Produces
    @LookupIfProperty(name = "beans.condition.active", stringValue = "A", lookupIfMissing = false)
    LookupIfPropertyBean conditionBeanA() {
        return new LookupIfPropertyBean("A");
    }

    /**
     * 根据配置项的属性值来确定是否产生对应的 Bean 对象
     *
     * <p>
     * {@link LookupUnlessProperty @LookupUnlessProperty} 注解表示当其 {@code name} 属性指定的配置项的值和 {@code stringValue}
     * 属性指定的值不相等时, 则会向 Quarkus 容器中放置该方法的返回值
     * </p>
     *
     * <p>
     * 参考 {@code resources:application.yml} 配置文件中的 {@code beans.condition.active} 配置项
     * </p>
     *
     * <p>
     * {@link LookupIfProperty @LookupIfProperty} 注解的 {@code lookupIfMissing} 属性表示, 如果指定的配置项缺失,
     * 是否实例化指定的 Bean
     * </p>
     *
     * @return {@link LookupIfPropertyBean} 类型对象
     */
    // @Singleton
    // @LookupUnlessProperty(name = "beans.condition.active", stringValue = "A", lookupIfMissing = false)
    // LookupIfPropertyBean conditionBeanB() {
    // return new LookupIfPropertyBean("B");
    // }

    @Produces
    @DefaultBean
    LookupIfPropertyBean conditionBeanDefault() {
        return new LookupIfPropertyBean("DEF");
    }

    /**
     * 根据 Profile 值来确定是否产生对应的 Bean 对象
     *
     * <p>
     * {@link IfBuildProfile @IfBuildProfile} 注解表示当当前项目的 Profile 值和注解指定的相等时, 则实例化指定的 Bean 对象,
     * 本方法表示, 当 Profile 值为 {@code dev} 时, 通过执行本方法实例化 Bean 对象
     * </p>
     *
     * <p>
     * 参考 {@code resources:application.yml} 配置文件中的 {@code quarkus.profile} 配置项.
     * 除配置项外, 当通过 {@code quarkus:dev} 启动项目时, Profile 自动设置为 {@code dev}; 当通过 {@code quarkus:test}
     * 执行项目时, Profile 自动设置为 {@code test}
     * </p>
     *
     * @return {@link IfBuildProfileBean} 类型对象
     */
    @Produces
    @IfBuildProfile("dev")
    IfBuildProfileBean conditionBeanDev() {
        return new IfBuildProfileBean("dev");
    }

    /**
     * 根据 Profile 值来确定是否产生对应的 Bean 对象
     *
     * <p>
     * {@link IfBuildProfile @IfBuildProfile} 注解表示当当前项目的 Profile 值和注解指定的相等时, 则实例化指定的 Bean 对象,
     * 本方法表示, 当 Profile 值不为 {@code dev} 时, 通过执行本方法实例化 Bean 对象
     * </p>
     *
     * <p>
     * 参考 {@code resources:application.yml} 配置文件中的 {@code quarkus.profile} 配置项.
     * 除配置项外, 当通过 {@code quarkus:dev} 启动项目时, Profile 自动设置为 {@code dev}; 当通过 {@code quarkus:test}
     * 执行项目时, Profile 自动设置为 {@code test}
     * </p>
     *
     * @return {@link IfBuildProfileBean} 类型对象
     */
    @Produces
    @UnlessBuildProfile("dev")
    IfBuildProfileBean conditionBeanTest() {
        return new IfBuildProfileBean("test");
    }
}
