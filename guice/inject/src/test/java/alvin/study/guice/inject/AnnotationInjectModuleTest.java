package alvin.study.guice.inject;

import alvin.study.guice.inject.AnnotationInjectModule.InjectByA;
import alvin.study.guice.inject.AnnotationInjectModule.InjectByB;
import com.google.inject.Module;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link AnnotationInjectModule} 模块
 */
class AnnotationInjectModuleTest extends BaseModuleTest {
    /**
     * 该类型的 {@code bean} 属性是通过 {@link alvin.study.inject.anno.A @A} 注解注入的
     */
    @Inject
    private InjectByA injectByA;

    /**
     * 该类型的 {@code bean} 属性是通过 {@link alvin.study.inject.anno.B @B} 注解注入的
     */
    @Inject
    private InjectByB injectByB;

    @Override
    protected Module getModule() { return new AnnotationInjectModule(); }

    /**
     * 确认通过不同注解注入的字段符合预期
     */
    @Test
    void inject_shouldBeanInjectByAnnotations() {
        then(injectByA.getBean().getValue()).isEqualTo("inject_by_a");
        then(injectByB.getBean().getValue()).isEqualTo("inject_by_b");
    }
}
