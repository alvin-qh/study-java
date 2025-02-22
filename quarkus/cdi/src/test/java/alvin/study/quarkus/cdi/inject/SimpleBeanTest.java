package alvin.study.quarkus.cdi.inject;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import alvin.study.quarkus.cdi.inject.anno.A;
import alvin.study.quarkus.cdi.inject.anno.B;
import io.quarkus.test.junit.QuarkusTest;

/**
 * 测试基本的 Bean 注入
 */
@QuarkusTest
class SimpleBeanTest {
    /**
     * 直接注入指定类型对象
     */
    @Inject
    SimpleBean simpleServiceDefault;

    /**
     * 通过 {@link A @A} 注解注入指定的对象
     *
     * <p>
     * {@link A @A} 注解是一个 {@link jakarta.inject.Qualifier Qualifier} 注解, 用于区分同类型不同的注入实例
     * </p>
     */
    @A
    @Inject
    SimpleBean simpleServiceA;

    /**
     * 通过 {@link B @B} 注解注入指定的对象
     *
     * <p>
     * {@link B @B} 注解是一个 {@link jakarta.inject.Qualifier Qualifier} 注解, 用于区分同类型不同的注入实例
     * </p>
     */
    @B
    @Inject
    SimpleBean simpleServiceB;

    /**
     * 测试
     */
    @Test
    void simpleBean_shouldInjectBeansWithQualifier() {
        // 测试直接注入的对象
        then(simpleServiceDefault.getName()).isEqualTo("Default");
        // 测试通过 @A 注解注入的对象
        then(simpleServiceA.getName()).isEqualTo("A");
        // 测试通过 @B 注解注入的对象
        then(simpleServiceB.getName()).isEqualTo("B");
    }

    /**
     * 通过 {@link A @A} 注解再次注入指定的对象
     *
     * <p>
     * 对于通过 {@link A @A} 注解注入的对象, 同时也使用了 {@link jakarta.inject.Singleton @Singleton} 注解, 所以无论注入多少次,
     * 注入的都是同一个对象
     * </p>
     */
    @A
    @Inject
    SimpleBean simpleServiceA2;

    /**
     * 通过 {@link B @B} 注解再次注入指定的对象
     *
     * <p>
     * 对于通过 {@link B @B} 注解注入的对象, 同时也使用了 {@link jakarta.enterprise.context.Dependent @Dependent} 注解,
     * 所以每次注入的都是一个新对象
     * </p>
     */
    @B
    @Inject
    SimpleBean simpleServiceB2;

    /**
     * 测试不同 Scope 注入对象生命周期的异同
     */
    @Test
    void simpleBean_makeSureDependentAnnotationCreateNewObjectEveryTime() {
        then(simpleServiceA2).isSameAs(simpleServiceA);
        then(simpleServiceB2).isNotSameAs(simpleServiceB);
    }
}
