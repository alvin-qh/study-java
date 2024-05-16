package alvin.study.guice.inject;

import alvin.study.guice.inject.bean.InjectDemo;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import lombok.Data;

/**
 * 通过 {@link Provider} 注入对象
 *
 * <p>
 * 对于一些需要较为复杂代码方能产生对象的情况, 可以在 {@link com.google.inject.Module Module} 类型中提供注解为
 * {@link Provides} 的方法, 这些方法会被 Guice 包装成为指定类型对象的"提供者"
 * </p>
 *
 * <p>
 * 使用这些 {@link Provider} 的方式为:
 * <ul>
 * <li>
 * 直接注入 {@link Provider} 类型对象, 在代码中通过 {@link Provider#get()} 方法获取目标类型对象
 * </li>
 * <li>
 * 直接注入目标类型对象, 自动通过 {@link Provider#get()} 方法获取目标类型对象
 * </li>
 * </ul>
 * </p>
 *
 * @see alvin.study.bind.ProviderBindingModule
 */
public class ProviderInjectModule extends AbstractModule {
    /**
     * 定义一个包装为 {@link Provider} 方法
     *
     * <p>
     * 通过 {@link Provides @Provides} 注解标记的方法会被包装为一个 {@link Provider} 接口对象
     * </p>
     *
     * @return 目标类型的对象
     */
    @Provides
    @Singleton
    public InjectDemo provideInjectDemo() {
        return new InjectDemo("inject_by_provider");
    }

    /**
     * 测试通过 {@link Provider} 注入目标对象
     *
     * <p>
     * 要注入的 Bean 对象将由一个 {@link Provider} 提供
     * </p>
     */
    @Data
    static class InjectValue {
        // 要注入的目标对象
        private final InjectDemo bean;

        /**
         * 通过构造器注入目标对象
         *
         * @param bean 通过 {@link Provider} 产生的 Bean 对象
         */
        @Inject
        public InjectValue(InjectDemo bean) {
            this.bean = bean;
        }
    }

    /**
     * 测试直接注入 {@link Provider} 对象
     */
    @Data
    static class InjectProvider {
        // 要注入的 Provider 对象
        private final Provider<InjectDemo> provider;

        /**
         * 通过构造器注入 {@link Provider} 对象
         */
        @Inject
        public InjectProvider(Provider<InjectDemo> provider) {
            this.provider = provider;
        }
    }
}
