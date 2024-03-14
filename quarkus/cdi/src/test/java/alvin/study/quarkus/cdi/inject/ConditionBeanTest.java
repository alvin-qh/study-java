package alvin.study.quarkus.cdi.inject;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * 测试条件注入
 */
@QuarkusTest
class ConditionBeanTest {
    /**
     * 在所有测试执行前执行
     *
     * <p>
     * 设置配置项 {@code beans.condition.active} 的值为 {@code B}, 此时 {@link ConditionBeanProduces} 类型中的
     * {@code conditionBeanB} 方法生效 (基于 {@link io.quarkus.arc.lookup.LookupUnlessProperty @LookupUnlessProperty}
     * 注解的作用)
     * </p>
     */
    @BeforeAll
    static void beforeAll() {
        // System.setProperty("beans.condition.active", "B");
    }

    /**
     * 注入 {@link LookupIfPropertyBean} 类型对象
     *
     * <p>
     * 对于通过 {@link io.quarkus.arc.lookup.LookupIfProperty @LookupIfProperty} 注解注释的 Bean, 需要通过
     * {@link Instance} 类型进行包装, 否则无法完成注入
     * </p>
     *
     * <p>
     * 另外, 对于无条件注解的普通 Bean 对象, 也可以通过 {@link Instance} 包装类进行注入, 只是并无必要
     * </p>
     */
    @Inject
    Instance<LookupIfPropertyBean> lookupIfPropertyBeanInstance;

    /**
     * 测试不同配置文件下 {@link LookupIfPropertyBean} 对象的注入
     *
     * <p>
     * 参见 {@link ConditionBeanProduces} 类定义可知, {@link LookupIfPropertyBean} 对象的注入是由配置文件
     * ({@code resources:application.yml}) 配置文件中的 {@code beans.condition.active} 配置项控制的
     * </p>
     *
     * <p>
     * 在所有测试开始前, 可以通过 {@link System#setProperty(String, String)} 方法修改配置文件的值. 但注意, 一旦容器初始化完毕,
     * 再通过 {@link System#setProperty(String, String)} 方法修改配置属性是无效的
     * </p>
     */
    @Test
    void lookupIfProperty_shouldInjectDifferentBeanByProperties() {
        var bean = lookupIfPropertyBeanInstance.get();
        then(bean.getName()).isEqualTo("A");
    }

    /**
     * 注入 {@link IfBuildProfileBean} 类型对象
     *
     * <p>
     * 对于通过 {@link io.quarkus.arc.profile.IfBuildProfile @IfBuildProfile} 注解注释的 Bean, 需要通过
     * {@link Instance} 类型进行包装, 否则无法完成注入
     * </p>
     *
     * <p>
     * 另外, 对于无条件注解的普通 Bean 对象, 也可以通过 {@link Instance} 包装类进行注入, 只是并无必要
     * </p>
     */
    @Inject
    Instance<IfBuildProfileBean> ifBuildProfileBeanInstance;

    /**
     * 测试在不同 Profile 情况下, {@link LookupIfPropertyBean} 对象的注入
     *
     * <p>
     * 参见 {@link ConditionBeanProduces} 类定义可知, {@link IfBuildProfileBean} 类型会在 Profile 不为 {@code dev} 时注入
     * {@code name} 属性为 {@code "test"} 的 {@link IfBuildProfileBean} 对象
     * </p>
     */
    @Test
    void ifBuildProfile_shouldInjectBeanByProfile() {
        var bean = ifBuildProfileBeanInstance.get();
        then(bean.getName()).isEqualTo("test");
    }
}
