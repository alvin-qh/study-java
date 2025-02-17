package alvin.study.guice.inject;

import alvin.study.guice.inject.ProviderInjectModule.InjectProvider;
import alvin.study.guice.inject.ProviderInjectModule.InjectValue;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link ProviderInjectModule} 模块
 *
 * <p>
 * 确认在模块中通过 {@link com.google.inject.Provides @Provides} 注解标记的方法可以被包装为一个
 * {@link jakarta.inject.Provider Provider} 对象
 * </p>
 *
 * <p>
 * 确认对于存在一个 {@link jakarta.inject.Provider Provider<T>} 类型时, 可以注入整个
 * {@link jakarta.inject.Provider Provider<T>} 对象, 也可以直接注入 {@code T} 类型对象,
 * 后者的特殊时会将 {@code T} 对象的实例化推迟到调用 {@link jakarta.inject.Provider#get()
 * Provider.get()} 方法的时候
 * </p>
 */

class ProviderInjectModuleTest extends BaseModuleTest {
    /**
     * 该对象中直接注入了目标 bean
     */
    @Inject
    private InjectValue injectValue;

    /**
     * 该对象中注入了目标 bean 的一个 {@link jakarta.inject.Provider Provider} 对象
     */
    @Inject
    private InjectProvider injectProvider;

    @Override
    protected Module getModule() { return new ProviderInjectModule(); }

    /**
     * 确认注入的对象或对象的 {@link jakarta.inject.Provider}
     */
    @Test
    void inject_shouldInjectValueOrProvider() {
        // 获取注入的对象
        var bean = injectValue.getBean();
        then(bean.getValue()).isEqualTo("inject_by_provider");

        // 获取注入的对象的 Provider
        var provider = injectProvider.getProvider();

        // 确认两中方式的结果是一致的
        then(provider.get()).isSameAs(bean);
        then(provider.get().getValue()).isEqualTo("inject_by_provider");
    }
}
