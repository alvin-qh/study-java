package alvin.study.guice.bind;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.google.inject.Module;

import alvin.study.guice.bind.ConstructorBindingModule.BindDemoImpl;
import alvin.study.guice.bind.inte.BindDemo;

/**
 * 测试 {@link ConstructorBindingModule} 类型
 */
class ConstructorBindingModuleTest extends BaseModuleTest {
    /**
     * 注入类型为 {@link BindDemoImpl} 类型对象
     */
    @Inject
    private BindDemo bindDemo;

    @Override
    protected Module getModule() { return new ConstructorBindingModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        then(bindDemo.test()).isEqualTo("constructor-bind-ConstructorBinding");
        then(bindDemo).isInstanceOf(BindDemoImpl.class);
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        var bindDemo = injector.getInstance(BindDemo.class);
        then(bindDemo).isSameAs(this.bindDemo);
    }
}
