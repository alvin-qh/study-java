package alvin.study.guice.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import alvin.study.guice.inject.bean.InjectDemo;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

/**
 * 通过命名注入同一个类型的不同对象
 *
 * @see alvin.study.bind.NamedBindingModule
 */
public class NamedInjectModule extends AbstractModule {
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
     * {@link Names#named(String)} 方法产生一个 {@link jakarta.inject.Named @Named} 注解对象,
     * 可以通过该对象获取绑定时设置的名称
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#annotatedWith(java.lang.annotation.Annotation)
     * AnnotatedBindingBuilder.annotatedWith(Named)} 方法表示将绑定的类型通过一个注解和目标进行绑定,
     * 即同一个类型可以通过不同的注解类型, 绑定到不同的目标上, 返回一个
     * {@link com.google.inject.binder.LinkedBindingBuilder LinkedBindingBuilder}
     * 接口对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.LinkedBindingBuilder#toInstance(Object)
     * LinkedBindingBuilder.toInstance(Object)} 方法表示将给定的泛型类型绑定到一个实际的对象上
     * </p>
     */
    @Override
    protected void configure() {
        // 绑定时使用名称: 将类型通过名称 "A" 绑定到实例上
        bind(InjectDemo.class).annotatedWith(Names.named("A"))
                .toInstance(new InjectDemo("inject_by_a"));
    }

    /**
     * 绑定时使用名称: 将一个 Provider 对象通过名称 {@code "B"} 进行绑定
     *
     * <p>
     * {@link Provides @Provides} 注解标识该方法是一个 {@link InjectDemo} 类型对象的提供器
     * </p>
     */
    @Named("B")
    @Provides
    @Singleton
    public InjectDemo injectDemoProvider() {
        return new InjectDemo("inject_by_b");
    }

    /**
     * 注入时使用名称: 通过名称 {@code "A"} 注入对象
     */
    @Getter
    @Singleton
    static class InjectByA {
        private final InjectDemo bean;

        /**
         * 通过构造器注入 {@link InjectDemo} 对象
         *
         * <p>
         * 名称 {@code "A"} 用于标识注入参数
         * </p>
         */
        @Inject
        public InjectByA(@Named("A") InjectDemo bean) {
            this.bean = bean;
        }
    }

    /**
     * 注入时使用名称: 通过名称 {@code "B"} 注入对象
     */
    @Getter
    static class InjectByB {
        // 通过字段注入被名称为 B 的实例对象
        @Inject
        @Named("B")
        private InjectDemo bean;
    }
}
