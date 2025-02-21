package alvin.study.guice.bind;

import java.util.Random;

import com.google.inject.AbstractModule;

import alvin.study.guice.bind.inte.BindDemo;
import jakarta.inject.Provider;

/**
 * 绑定 Provider
 *
 * <p>
 * 可以将一个类型和一个 Provider 类型绑定, 从而"产生出"所需的对象
 * </p>
 *
 * <p>
 * 所谓绑定, 即将一个抽象类型通过某种范围定义, 和其具体的实现类型或实现类型的一个具体实例进行绑定, 如此以来当需要注入该抽象类型的实例时,
 * Guice 框架会从容器中找寻最符合注入场景范围定义的那个被绑定的类型或实例, 并将其产生的实例对象 (或其实例对象本身) 注入到目标对象中
 * </p>
 *
 * <p>
 * {@link com.google.inject.Module Module} 接口作为 Guice Bean 管理的一个<b>模块</b>,
 * 定义了各种类型的绑定关系, 并最终交给 Guice 容器管理, 不同的 {@link com.google.inject.Module Module}
 * 的边界也不同, 不会相互干扰.
 * {@link com.google.inject.Module#configure(com.google.inject.Binder)
 * Module.configure(Binder)} 方法用于对当前模块所需要定义的绑定关系进行配置
 * </p>
 *
 * <p>
 * {@link AbstractModule} 是 {@link com.google.inject.Module Module} 接口的一个抽象实线,
 * 提供了一系列便捷的工具方法协助对象的绑定和管理操作, 其中的 {@link AbstractModule#configure()}
 * 是 {@link com.google.inject.Module#configure(com.google.inject.Binder)
 * Module.configure(Binder)} 方法的一个简化操作版本
 * </p>
 *
 * <p>
 * {@link com.google.inject.Module Module} 类型的实例可以作为构建
 * {@link com.google.inject.Injector Injector} 注入器实例对象的参数, 通过
 * {@link com.google.inject.Guice#createInjector(com.google.inject.Module...)
 * Guice.createInjector(Module...)} 方法创建的注入器对象可以根据模块设置的绑定关系, 对目标对象进行注入操作, 即通过
 * {@link com.google.inject.Injector#injectMembers(Object)
 * Injector.injectMembers(Object)} 方法将绑定关系中定义的实例注入到目标对象的字段中
 * </p>
 *
 * @see alvin.study.inject.ProviderInjectModule
 */
public class ProviderBindingModule extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * {@link AbstractModule#bind(Class)} 用于绑定一个类型, 返回一个
     * {@link com.google.inject.binder.AnnotatedBindingBuilder
     * AnnotatedBindingBuilder} 接口对象, 用于进一步配置类型绑定的目标
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#toProvider(Class)
     * AnnotatedBindingBuilder.toProvider(Class)} 方法表示将给定的类型绑定到一个实际的类型上,
     * {@code toProvider} 方法的参数是一个 {@code Class<? extends Provider<? extends T>>}
     * 类型的参数, 返回一个 {@link com.google.inject.binder.ScopedBindingBuilder
     * ScopedBindingBuilder} 类型的对象, 可用于对绑定关系的应用范围做进一步设置
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.ScopedBindingBuilder#asEagerSingleton()
     * ScopedBindingBuilder.asEagerSingleton} 表示最终注入的对象为单例模式对象
     * </p>
     *
     * <p>
     * <code>bind(BindDemo.class).toProvider(BindDemoProvider.class)</code>
     * 这段代码说明的是将 {@link BindDemo} 类型绑定到 {@link BindDemoProvider} 类型上, 由
     * {@link BindDemoProvider#get()} 方法提供 {@link BindDemo} 类型对象
     * </p>
     */
    @Override
    protected void configure() {
        bind(BindDemo.class)
                .toProvider(BindDemoProvider.class)
                .asEagerSingleton();
    }

    /**
     * 用于提供 BindDemo 类型对象的 Provider
     */
    static class BindDemoProvider implements Provider<BindDemo> {
        private final Random random = new Random();

        /**
         * 获取 {@link BindDemo} 类型对象
         *
         * @return {@link BindDemo} 类型对象
         */
        @Override
        public BindDemo get() {
            return new BindDemoImpl(Math.abs(random.nextInt(10000)));
        }
    }

    /**
     * 待注入的类型
     */
    static class BindDemoImpl implements BindDemo {
        private final int id;

        public BindDemoImpl(int id) {
            this.id = id;
        }

        @Override
        public String test() {
            return String.format("provider-bind-demo(%d)", id);
        }
    }
}
