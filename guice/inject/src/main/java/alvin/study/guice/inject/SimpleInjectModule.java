package alvin.study.guice.inject;

import alvin.study.guice.inject.bean.InjectDemo;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Inject;
import lombok.Getter;

/**
 * 基本对象注入
 *
 * <p>
 * 通过 {@link Inject @Inject} 注解可以通过 Guice 容器将期望的值注入到目标对象的字段中
 * </p>
 *
 * @see alvin.study.bind.SimpleBindingModule
 */
public class SimpleInjectModule extends AbstractModule {
    /**
     * 以 {@link jakarta.inject.Provider Provider} 方法提供被注入对象
     */
    @Provides
    public InjectDemo provideInjectDemo() {
        return new InjectDemo("inject_demo");
    }

    /**
     * 以字段方式注入目标对象
     *
     * <p>
     * 在类字段上设置 {@link Inject @Inject} 注解, 表示目标对象直接注入到类字段上
     * </p>
     */
    @Getter
    static class InjectorByField {
        /**
         * 接受注入的类字段
         */
        @Inject
        private InjectDemo bean;
    }

    /**
     * 以构造器方式注入目标对象
     *
     * <p>
     * 在类构造器方法上设置 {@link Inject @Inject} 注解, 表示目标对象通过构造器参数进行注入
     * </p>
     */
    @Getter
    static class InjectorByConstruct {
        private final InjectDemo bean;

        /**
         * 接受参数注入的构造器方法
         *
         * @param bean 注入的参数
         */
        @Inject
        public InjectorByConstruct(InjectDemo bean) {
            this.bean = bean;
        }
    }

    /**
     * 在属性的 {@code set} 方法上注入目标对象
     *
     * <p>
     * 在 set 方法上设置 {@link Inject @Inject} 注解, 表示目标对象通过 set 方法参数进行注入
     * </p>
     */
    @Getter
    static class InjectorBySetter {
        private InjectDemo bean;

        /**
         * 接受参数注入的 set 方法
         *
         * @param bean 注入的参数
         */
        @Inject
        public void setInjector(InjectDemo bean) { this.bean = bean; }
    }
}
