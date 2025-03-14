package alvin.study.guice.inject;

import jakarta.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import lombok.Data;
import lombok.Getter;

import alvin.study.guice.inject.anno.A;
import alvin.study.guice.inject.anno.B;
import alvin.study.guice.inject.bean.InjectDemo;

/**
 * 通过注解绑定接口的不同类型
 *
 * @see alvin.study.bind.AnnotationBindingModule
 */
public class AnnotationInjectModule extends AbstractModule {
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
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#annotatedWith(Class)
     * AnnotatedBindingBuilder.annotatedWith(Class&lt;? extends Annotation&gt;)}
     * 表示绑定范围由一个注解来确定, 即同一个类型可以通过不同的注解类型, 绑定到不同的目标上,
     * 返回一个 {@link com.google.inject.binder.LinkedBindingBuilder
     * LinkedBindingBuilder} 接口对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.LinkedBindingBuilder#toInstance(Object)
     * LinkedBindingBuilder.toInstance(Object)}
     * 方法表示将给定的泛型类型绑定到一个实际的对象上
     * </p>
     *
     * <p>
     * <code>
     * binder.bind(BindDemo.class).annotatedWith(A.class).toInstance(new InjectDemo(...))
     * </code>
     * 这段代码说明的是, 当注入的 {@link InjectDemo} 类型字段时, 实际会注入该类型的具体对象
     * </p>
     */
    @Override
    protected void configure() {
        // 绑定时使用注解: 将类型通过注解 A 绑定到实例上
        bind(InjectDemo.class)
                .annotatedWith(A.class)
                .toInstance(new InjectDemo("inject_by_a"));
    }

    /**
     * 绑定时使用注解: 将一个 Provider 对象通过 {@link B @B} 注解进行绑定
     *
     * <p>
     * {@link Provides @Provides} 注解标识该方法是一个 {@link InjectDemo}
     * 类型对象的提供器
     * </p>
     */
    @B
    @Provides
    @Singleton
    public InjectDemo bindByBProvider() {
        return new InjectDemo("inject_by_b");
    }

    /**
     * 注入时使用注解: 通过 {@link A @A} 注解标识注入对象
     */
    @Data
    @Singleton
    static class InjectByA {
        private final InjectDemo bean;

        /**
         * 通过构造器注入 {@link InjectDemo} 对象
         *
         * <p>
         * {@link A @A} 注解标识用于标识注入参数
         * </p>
         */
        @Inject
        public InjectByA(@A InjectDemo bean) {
            this.bean = bean;
        }
    }

    /**
     * 注入时使用注解: 通过 {@link B @B} 注解标识注入对象
     */
    @Getter
    @Singleton
    static class InjectByB {
        /**
         * 通过字段注入 {@link InjectDemo} 对象
         *
         * <p>
         * {@link B @B} 注解标识用于标识注入参数
         * </p>
         */
        @B
        @Inject
        private InjectDemo bean;
    }
}
