package alvin.study.guice.bind;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

import org.junit.jupiter.api.Test;

import alvin.study.guice.bind.NamedBindingModule.BindDemoA;
import alvin.study.guice.bind.NamedBindingModule.BindDemoB;
import alvin.study.guice.bind.inte.BindDemo;

/**
 * 测试 {@link NamedBindingModule} 类型
 */
class NamedBindingModuleTest extends BaseModuleTest {
    /**
     * {@code @Named("A")} 表示被注解的对象类型为 {@link BindDemoA} 类型
     */
    @Named("A")
    @Inject
    private BindDemo bindDemoA;

    /**
     * {@code @Named("B")} 表示被注解的对象类型为 {@link BindDemoB} 类型
     */
    @Named("B")
    @Inject
    private BindDemo bindDemoB;

    @Override
    protected Module getModule() { return new NamedBindingModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        then(bindDemoA).isInstanceOf(BindDemoA.class);
        then(bindDemoA.test()).isEqualTo("named-bind-class-a");

        then(bindDemoB).isInstanceOf(BindDemoB.class);
        then(bindDemoB.test()).isEqualTo("named-bind-class-b");
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        var bindDemoA = injector.getInstance(
            Key.get(BindDemo.class, Names.named("A")));
        var bindDemoB = injector.getInstance(
            Key.get(BindDemo.class, Names.named("B")));

        then(bindDemoA).isSameAs(this.bindDemoA);
        then(bindDemoB).isSameAs(this.bindDemoB);
    }
}
