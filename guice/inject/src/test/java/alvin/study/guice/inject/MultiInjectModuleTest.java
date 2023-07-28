package alvin.study.guice.inject;

import alvin.study.guice.inject.bean.InjectDemo;
import com.google.inject.Module;
import com.google.inject.name.Named;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link MultiInjectModule} 模块
 *
 * <p>
 * 确认通过 {@link com.google.inject.multibindings.Multibinder Multibinder} 和
 * {@link com.google.inject.multibindings.MapBinder MapBinder} 注入符合绑定关系的实例集合
 * </p>
 */
class MultiInjectModuleTest extends BaseModuleTest {
    /**
     * 注入 {@link com.google.inject.multibindings.Multibinder Multibinder} 绑定的
     * {@link Set} 对象集合
     */
    @Inject
    @Named("boundSet")
    private Set<Integer> boundSet;

    /**
     * 注入 {@link com.google.inject.multibindings.MapBinder MapBinder} 绑定的
     * {@link Map} 对象集合
     */
    @Inject
    @Named("boundMap")
    private Map<String, InjectDemo> boundMap;

    @Override
    protected Module getModule() {
        return new MultiInjectModule();
    }

    /**
     * 确认注入的集合内容符合设定的绑定关系
     */
    @Test
    void inject_shouldMultiBindingsInject() {
        then(boundSet).containsExactlyInAnyOrder(100, 200, 300);

        then(boundMap.get("A").getValue()).isEqualTo("toInstanceA");
        then(boundMap.get("B").getValue()).isEqualTo("defaultValue");
        then(boundMap.get("C").getValue()).isEqualTo("toInstanceC");
    }
}
