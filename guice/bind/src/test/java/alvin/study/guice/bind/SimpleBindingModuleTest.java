package alvin.study.guice.bind;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;

import com.google.inject.Module;

import org.junit.jupiter.api.Test;

import alvin.study.guice.bind.SimpleBindingModule.BindDemoImpl;
import alvin.study.guice.bind.inte.BindDemo;

/**
 * 测试 {@link ProviderBindingModule} 类型
 */
class SimpleBindingModuleTest extends BaseModuleTest {
    /**
     * 注入 {@link BindDemoImpl} 类型对象
     */
    @Inject
    private BindDemo bindDemo;

    @Override
    protected Module getModule() { return new SimpleBindingModule(); }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldInjectBean() {
        then(bindDemo).isInstanceOf(BindDemoImpl.class);
        then(bindDemo.test()).isEqualTo("simple-bind-demo");
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldInjectBeanProgramming() {
        var bindDemo = injector.getInstance(BindDemo.class);
        then(bindDemo).isSameAs(this.bindDemo);
    }
}
