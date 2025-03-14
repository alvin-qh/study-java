package alvin.study.guice.bind;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import alvin.study.guice.bind.inte.BindDemo;

/**
 * 为绑定关系命名
 *
 * <p>
 * 和通过注解标记绑定关系类似, 可以通过命名的方式为绑定关系设置名称,
 * 从而在注入的时候通过不同的名称诸如不同的实例.
 * 主要是为了解决同一类型不同实例的绑定和注入问题
 * </p>
 *
 * <p>
 * 所谓绑定, 即将一个抽象类型通过某种范围定义, 和其具体的实现类型或实现类型的一个具体实例进行绑定,
 * 如此以来当需要注入该抽象类型的实例时, Guice
 * 框架会从容器中找寻最符合注入场景范围定义的那个被绑定的类型或实例, 并将其产生的实例对象
 * (或其实例对象本身) 注入到目标对象中
 * </p>
 *
 * <p>
 * {@link com.google.inject.Module Module} 接口作为 Guice Bean 管理的一个
 * <b>模块</b>, 定义了各种类型的绑定关系, 并最终交给 Guice 容器管理, 不同的
 * {@link com.google.inject.Module Module} 的边界也不同, 不会相互干扰.
 * {@link com.google.inject.Module#configure(com.google.inject.Binder)
 * Module.configure(Binder)} 方法用于对当前模块所需要定义的绑定关系进行配置
 * </p>
 *
 * <p>
 * {@link AbstractModule} 是 {@link com.google.inject.Module Module}
 * 接口的一个抽象实线, 提供了一系列便捷的工具方法协助对象的绑定和管理操作, 其中的
 * {@link AbstractModule#configure()} 是
 * {@link com.google.inject.Module#configure(com.google.inject.Binder)
 * Module.configure(Binder)} 方法的一个简化操作版本
 * </p>
 *
 * <p>
 * {@link com.google.inject.Module Module} 类型的实例可以作为构建
 * {@link com.google.inject.Injector Injector} 注入器实例对象的参数, 通过
 * {@link com.google.inject.Guice#createInjector(com.google.inject.Module...)
 * Guice.createInjector(Module...)} 方法创建的注入器对象可以根据模块设置的绑定关系,
 * 对目标对象进行注入操作, 即通过 {@link com.google.inject.Injector#injectMembers(Object)
 * Injector.injectMembers(Object)} 方法将绑定关系中定义的实例注入到目标对象的字段中
 * </p>
 *
 * @see alvin.study.bind.AnnotationBindingModule
 * @see alvin.study.inject.NamedInjectModule
 */
public class NamedBindingModule extends AbstractModule {
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
     * {@link Names#named(String)} 方法产生一个 {@link jakarta.inject.Named @Named}
     * 注解对象, 可以通过该对象获取绑定时设置的名称
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#annotatedWith(
     * java.lang.annotation.Annotation) AnnotatedBindingBuilder.annotatedWith(Named)}
     * 方法表示将绑定的类型通过一个注解和目标进行绑定, 即同一个类型可以通过不同的注解类型,
     * 绑定到不同的目标上, 返回一个 {@link com.google.inject.binder.LinkedBindingBuilder
     * LinkedBindingBuilder} 接口对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.LinkedBindingBuilder#to(Class)
     * LinkedBindingBuilder.to(Class)} 方法表示将给定的类型绑定到一个实际类型上, 返回一个
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
     * <code>
     * bind(BindDemo.class).annotatedWith(Names.named("A")).to(BindDemoA.class)
     * </code>
     * 这段代码说明的是将 {@link BindDemo} 接口通过名称 {@code "A"} 绑定到
     * {@link BindDemoA} 类型上, 所以当注入同样标记为 {@code "A"} 的字段时, 会注入
     * {@link BindDemoA} 类型对象
     * </p>
     */
    @Override
    protected void configure() {
        // 通过名称 A 将 BindDemo 接口绑定在 BindDemoA 类型
        bind(BindDemo.class)
                .annotatedWith(Names.named("A"))
                .to(BindDemoA.class)
                .asEagerSingleton();

        // 通过名称 B 将 BindDemo 接口绑定在 BindDemoB 类型
        bind(BindDemo.class)
                .annotatedWith(Names.named("B"))
                .to(BindDemoB.class)
                .asEagerSingleton();
    }

    /**
     * 待注入的类型
     */
    static class BindDemoA implements BindDemo {
        @Override
        public String test() {
            return "named-bind-class-a";
        }
    }

    /**
     * 待注入的类型
     */
    static class BindDemoB implements BindDemo {
        @Override
        public String test() {
            return "named-bind-class-b";
        }
    }
}
