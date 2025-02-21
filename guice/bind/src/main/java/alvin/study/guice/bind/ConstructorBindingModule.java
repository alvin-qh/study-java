package alvin.study.guice.bind;

import jakarta.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import alvin.study.guice.bind.inte.BindDemo;

/**
 * 通过<b>构造器</b>进行绑定
 *
 * <p>
 * 一些时候, 某个类型的实现具备多个构造器, 在产生对象时无法确定使用哪个构造器. 此时, 可以将类型和目标类型的指定构造器进行绑定,
 * 通过被绑定的构造器产生所需的对象
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
 */
public class ConstructorBindingModule extends AbstractModule {
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
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#toConstructor(java.lang.reflect.Constructor)
     * AnnotatedBindingBuilder.toConstructor(Constructor)} 方法表示将绑定的类型通过一个注解和目标进行绑定,
     * 即当目标类型有多个构造器方法时, 选择哪个对目标类型进行实例化, 返回一个
     * {@link com.google.inject.binder.ScopedBindingBuilder ScopedBindingBuilder}
     * 类型的对象, 可用于对绑定关系的应用范围做进一步设置
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.ScopedBindingBuilder#asEagerSingleton()
     * ScopedBindingBuilder.asEagerSingleton} 表示最终注入的对象为单例模式对象
     * </p>
     *
     * <p>
     * {@code bind(BindDemo.class).toConstructor(constructor)} 这段代码说明的是, 当注入的
     * {@link BindDemo} 类型字段时, 会通过给定的构造器方法对象实例化实际的 {@link BindDemoImpl} 类型对象
     * </p>
     */
    @Override
    protected void configure() {
        try {
            // 获取待注入类型的构造器对象
            var constructor = BindDemoImpl.class.getConstructor(String.class);
            // 通过构造器将 BindDemo 接口和 BindDemoImpl 类型进行绑定
            bind(BindDemo.class)
                    .toConstructor(constructor)
                    .asEagerSingleton();

            // 将标记为 "Value" 的字符串注入到 BindDemoImpl 的构造器参数中
            bind(String.class)
                    .annotatedWith(Names.named("Value"))
                    .toInstance("ConstructorBinding");
        } catch (Exception e) {
            addError(e);
        }
    }

    /**
     * 带注入的类型
     */
    @Singleton
    static class BindDemoImpl implements BindDemo {
        private final String value;

        /**
         * 用于注入操作的构造器
         *
         * @param value 标记为 "Value", 通过同样标记为 "Value" 的绑定对象进行注入
         */
        public BindDemoImpl(@Named("Value") String value) {
            this.value = value;
        }

        @Override
        public String test() {
            return String.format("constructor-bind-%s", value);
        }
    }
}
