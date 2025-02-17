package alvin.study.guice.bind;

import alvin.study.guice.bind.inte.BindDemo;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link MultiBinderModule} 类型
 */
class MultiBinderModuleTest extends BaseModuleTest {
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
    private Map<String, BindDemo> boundMap;

    @Override
    protected Module getModule() { return new MultiBinderModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        then(boundSet).containsExactlyInAnyOrder(100, 200, 300);

        then(boundMap.get("A").test()).isEqualTo("toInstanceA");
        then(boundMap.get("B").test()).isEqualTo("toInstanceB");
        then(boundMap.get("C").test()).isEqualTo("toInstanceC");
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        var boundSet = injector.getInstance(
            Key.get(new TypeLiteral<Set<Integer>>() {}, Names.named("boundSet")));

        var boundMap = injector.getInstance(
            Key.get(new TypeLiteral<Map<String, BindDemo>>() {}, Names.named("boundMap")));

        then(boundSet).isEqualTo(this.boundSet);
        then(boundMap).isEqualTo(this.boundMap);
    }
}
