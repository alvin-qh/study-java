package alvin.study.guice.inject;

import alvin.study.guice.inject.NamedInjectModule.InjectByA;
import alvin.study.guice.inject.NamedInjectModule.InjectByB;
import com.google.inject.Module;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link NamedInjectModule} 模块
 *
 * <p>
 * 确认可以通过 {@link com.google.inject.name.Names#named(String) Names.named(String)}
 * 为绑定关系命名, 并通过 {@link jakarta.inject.Named @Named} 注解和命名进行注入
 * </p>
 */
class NamedInjectModuleTest extends BaseModuleTest {
    /**
     * 该类型的 {@code bean} 属性是通过 {@link jakarta.inject.Named @Named("A")} 名称注入的
     */
    @Inject
    private InjectByA injectByA;

    /**
     * 该类型的 {@code bean} 属性是通过 {@link jakarta.inject.Named @Named("B")} 名称注入的
     */
    @Inject
    private InjectByB injectByB;

    @Override
    protected Module getModule() {
        return new NamedInjectModule();
    }

    /**
     * 确认通过不同名称注入的字段符合预期
     */
    @Test
    void inject_shouldBeanInjectedByNames() {
        // 测试被命名为 A 的对象注入情况
        then(injectByA.getBean().getValue()).isEqualTo("inject_by_a");

        // 测试被命名为 B 的对象注入情况
        then(injectByB.getBean().getValue()).isEqualTo("inject_by_b");
    }
}
