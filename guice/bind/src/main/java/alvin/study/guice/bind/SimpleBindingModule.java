package alvin.study.guice.bind;

import jakarta.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;

import alvin.study.guice.bind.inte.BindDemo;

/**
 * 基本绑定演示
 *
 * <p>
 * 将一个类型 {@code C1} 绑定到另一个类型 {@code C2} 上, 例如将一个接口类型绑定到其实现类类型上.
 * 当注入 {@code C1} 类型字段时, 会生成 {@code C2} 类型的对象并注入
 * </p>
 *
 * <p>
 * {@link Module Module} 接口作为 Guice Bean 管理的一个<b>模块</b>, 对一组
 * Bean 对象进行管理, 不同的 {@link Module Module} 对象管理的边界也不同,
 * 其中的 Bean 对象不会相互干扰. {@link Module#configure(Binder)
 * Module.configure(Binder)} 方法用于对模块进行配置
 * </p>
 *
 * @see alvin.study.inject.SimpleInjectModule
 */
public class SimpleBindingModule implements Module {
    /**
     * 配置模块
     *
     * <p>
     * {@code AbstractModule.bind(Class)} 用于绑定一个类型, 返回一个
     * {@link com.google.inject.binder.AnnotatedBindingBuilder
     * AnnotatedBindingBuilder} 接口对象, 用于进一步配置类型绑定的目标
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#to(Class)
     * AnnotatedBindingBuilder.to(Class)} 方法表示将给定的类型绑定到另一个类型上,
     * 返回一个 {@link com.google.inject.binder.ScopedBindingBuilder
     * ScopedBindingBuilder} 类型的对象, 可用于对绑定关系的应用范围做进一步设置
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.ScopedBindingBuilder#asEagerSingleton()
     * ScopedBindingBuilder.asEagerSingleton} 表示最终注入的对象为单例模式对象
     * </p>
     *
     * <p>
     * <code>
     * binder.bind(BindDemo.class).to(BindDemoImpl.class)
     * </code>
     * 这段代码说明的是, 当注入的 {@link BindDemo} 类型字段时, 实际会注入一个具体的
     * {@link BindDemoImpl} 目标对象类型对象
     * </p>
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(BindDemo.class)
                .to(BindDemoImpl.class)
                .asEagerSingleton();
    }

    /**
     * 待注入的类型
     */
    @Singleton
    static class BindDemoImpl implements BindDemo {
        @Override
        public String test() {
            return "simple-bind-demo";
        }
    }
}
