package alvin.study.guice.bind;

import alvin.study.guice.bind.ProviderBindingModule.BindDemoImpl;
import alvin.study.guice.bind.inte.BindDemo;
import com.google.inject.Module;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link ProviderBindingModule} 类型
 */
class ProviderBindingModuleTest extends BaseModuleTest {
    /**
     * 注入通过 {@code Provider<BindDemo>} 产生的对象
     */
    @Inject
    private BindDemo bindDemo;

    /**
     * 注入 {@code Provider<BindDemo>} 对象本身
     */
    @Inject
    private Provider<BindDemo> provider;

    @Override
    protected Module getModule() {
        return new ProviderBindingModule();
    }

    /**
     * 测试注入的对象符合预期
     */
    @Test
    void bind_shouldObjectInjected() {
        then(bindDemo).isInstanceOf(BindDemoImpl.class);
        then(bindDemo.test()).startsWith("provider-bind-demo");
        then(provider.get()).isSameAs(bindDemo);
    }

    /**
     * 测试通过编程获取目标对象
     */
    @Test
    void bind_shouldGetObjectByInjector() {
        var bindDemo = injector.getInstance(BindDemo.class);
        var provider = injector.getProvider(BindDemo.class);

        then(bindDemo).isSameAs(this.bindDemo);
        then(provider.get()).isSameAs(this.bindDemo);
    }
}
