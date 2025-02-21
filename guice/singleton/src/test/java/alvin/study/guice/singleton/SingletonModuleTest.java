package alvin.study.guice.singleton;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;

import org.junit.jupiter.api.Test;

import com.google.inject.Guice;

import alvin.study.guice.singleton.bean.SingletonAnnotationBean;
import alvin.study.guice.singleton.bean.SingletonBean;

/**
 * 测试 {@link SingletonModule} 模块
 *
 * <p>
 * 当调用 {@link SingletonModule#SingletonModule(boolean)} 构造器并传入 {@code true} 时,
 * {@link SingletonBean} 和 {@link Provider Provider<SingletonBean>} 的绑定关系为单例模式;
 * 当传入 {@code false} 时, 绑定为非单例模式
 * </p>
 *
 * <p>
 * 对于 {@link SingletonAnnotationBean} 类型, 由于其标识了
 * {@link jakarta.inject.Singleton @Singleton} 注解, 所以无论
 * {@link SingletonModule#SingletonModule(boolean)} 构造器的参数为何, 该类型一直为单例模式
 * </p>
 */
class SingletonModuleTest {
    /**
     * {@code annotationBean1} 和 {@code annotationBean2} 一直为单例模式
     */
    @Inject
    private SingletonAnnotationBean annotationBean1;

    @Inject
    private SingletonAnnotationBean annotationBean2;

    /**
     * {@code annotationBean1} 和 {@code annotationBean2} 会根据
     * {@link SingletonModule#SingletonModule(boolean)} 构造器参数的值, 绑定为单例或非单例模式
     */
    @Inject
    @Named("Bean")
    private SingletonBean bean1;

    @Inject
    @Named("Bean")
    private SingletonBean bean2;

    /**
     * {@code provider1} 和 {@code provider2} 会根据
     * {@link SingletonModule#SingletonModule(boolean)} 构造器参数的值, 绑定为单例或非单例模式的
     * {@link Provider}
     */
    @Inject
    @Named("Provider")
    private Provider<SingletonBean> provider1;

    @Inject
    @Named("Provider")
    private Provider<SingletonBean> provider2;

    /**
     * 确认 {@code annotationBean1} 和 {@code annotationBean2} 两个字段的单例情况
     */
    @Test
    void singleton_shouldInjectBeanWithSingletonAnnotation() {
        Guice.createInjector(new SingletonModule(true)).injectMembers(this);
        then(annotationBean1).isSameAs(annotationBean2);

        Guice.createInjector(new SingletonModule(false)).injectMembers(this);
        then(annotationBean1).isSameAs(annotationBean2);
    }

    /**
     * 确认 {@code bean1} 和 {@code bean2} 两个字段的单例情况
     */
    @Test
    void singleton_shouldInjectBean() {
        // 使用单例模式
        Guice.createInjector(new SingletonModule(true)).injectMembers(this);
        // bean1 和 bean2 代表同一个对象
        then(bean1).isSameAs(bean2);

        // 不使用单例模式
        Guice.createInjector(new SingletonModule(false)).injectMembers(this);
        // bean1 和 bean2 代表不同的对象
        then(bean1).isNotSameAs(bean2);
    }

    /**
     * 确认 {@code provider1} 和 {@code provider2} 获取对象的单例情况
     */
    @Test
    void shouldInjectBeanProvider() {
        // 使用单例模式
        Guice.createInjector(new SingletonModule(true)).injectMembers(this);
        // provider1 和 provider2 返回同一个对象
        then(provider1.get()).isSameAs(provider2.get());

        // 不使用单例模式
        Guice.createInjector(new SingletonModule(false)).injectMembers(this);
        // provider1 和 provider2 返回不同对象
        then(provider1.get()).isNotSameAs(provider2.get());
    }
}
