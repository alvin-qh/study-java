package alvin.study.guice.bind;

import jakarta.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import alvin.study.guice.bind.inte.BindDemo;

/**
 * 多实例绑定
 *
 * <p>
 * 多实例绑定, 即把同一类型 (或名称) 的实例绑定在一个集合对象中. 根据需要不同, 具备 {@link Multibinder} 和
 * {@link MapBinder} 两种方式可以选择, 前者的注入结果是一个 {@link java.util.Set Set} 集合, 后者是一个
 * {@link java.util.Map Map} 集合
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
 * @see alvin.study.inject.MultiInjectModule
 */
public class MultiBinderModule extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * {@link Multibinder} 对象中可以添加多个绑定关系, 并在容器中产生一个 {@link java.util.Set} 集合, 注入该集合,
     * 集合中的各个元素即为通过设置的绑定关系产生的对象实例
     * </p>
     *
     * <p>
     * {@link MapBinder} 的作用和 {@link MapBinder} 类似, 最终注入为一个 {@link java.util.Map
     * Map} 集合, 区别是给了每个绑定关系一个 {@code Key} 值
     * </p>
     *
     * <p>
     * {@link Multibinder#newSetBinder(com.google.inject.Binder, Class, java.lang.annotation.Annotation)
     * Multibinder.newSetBinder(Binder, Class, Annotation)} 用于产生一个
     * {@link Multibinder} 实例对象
     * </p>
     *
     * <p>
     * {@link MapBinder#newMapBinder(com.google.inject.Binder, Class, Class, java.lang.annotation.Annotation)
     * MapBinder.newMapBinder(Binder, Class, Class, Annotation)} 用于产生一个
     * {@link MapBinder} 实例对象
     * </p>
     *
     * <p>
     * {@link Multibinder#addBinding()} 用于添加一个绑定关系, 返回一个
     * {@link com.google.inject.binder.LinkedBindingBuilder LinkedBindingBuilder} 对象
     * </p>
     *
     * <p>
     * {@link MapBinder#addBinding(Object)} 用于添加一个绑定关系, 并给出一个 {@code Key} 值作为标识,
     * 返回一个 {@link com.google.inject.binder.LinkedBindingBuilder
     * LinkedBindingBuilder} 对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.LinkedBindingBuilder#toInstance(Object)
     * LinkedBindingBuilder.toInstance(Object)} 创建一个绑定关系, 类似的方法还包括:
     * {@link com.google.inject.binder.LinkedBindingBuilder#to(Class)
     * LinkedBindingBuilder.to(Class)},
     * {@link com.google.inject.binder.LinkedBindingBuilder#toConstructor(java.lang.reflect.Constructor)
     * LinkedBindingBuilder.toConstructor(Constructor)},
     * {@link com.google.inject.binder.LinkedBindingBuilder#toProvider(Provider)
     * LinkedBindingBuilder.toProvider(Provider)} 等方法
     * </p>
     */
    @Override
    protected void configure() {
        // 一个绑定多个整数实例的 Set 集合
        var boundSet = Multibinder.newSetBinder(binder(), Integer.class, Names.named("boundSet"));
        boundSet.addBinding().toInstance(100); // 为 Set 集合中绑定对象实例
        boundSet.addBinding().toInstance(200); // 为 Set 集合中绑定对象实例
        boundSet.addBinding().toProvider(() -> 300); // 为 Set 集合中绑定 Provider

        // 一个绑定多个实例的 Map 集合
        var boundMap = MapBinder.newMapBinder(binder(), String.class, BindDemo.class, Names.named("boundMap"));
        boundMap.addBinding("A").toInstance(new BindDemoImpl("toInstanceA")); // 将对象实例绑定在 Key A 上
        boundMap.addBinding("B").to(BindDemoImpl.class); // 将类型绑定在 Key B 上
        boundMap.addBinding("C").toProvider(() -> new BindDemoImpl("toInstanceC")).asEagerSingleton();   // 将 Provider
                                                                                                         // 绑定在 Key C 上
    }

    /**
     * 用于绑定的演示类型
     */
    @Singleton
    static class BindDemoImpl implements BindDemo {
        private final String value;

        public BindDemoImpl() {
            this("toInstanceB");
        }

        public BindDemoImpl(String value) {
            this.value = value;
        }

        @Override
        public String test() {
            return this.value;
        }
    }
}
