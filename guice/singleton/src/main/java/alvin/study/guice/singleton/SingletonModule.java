package alvin.study.guice.singleton;

import alvin.study.guice.singleton.bean.SingletonBean;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import jakarta.inject.Singleton;

/**
 * 单例是一种设计模式和代码设计技巧
 *
 * <p>
 * 对于"无状态"的类 (即类本身的属性值不会发生变化), 则可以构建为单例, 使其在内存中只存在一份, 可以减少内存的消耗, 以及减少初始化对象的时间损耗
 * </p>
 *
 * <p>
 * 单例对象一般会进行"延时生成"处理, 即第一次用到该对象时, 才会进行生成, 一旦生成后便一直以单例形式存在. 但也有部分情况,
 * 需要在容器初始化时进行生成, 而不关心什么时候使用该对象
 * </p>
 *
 * <p>
 * Guice 容器对于默认类型的处理是"非单例"的, 通过 {@code bind(...).asEagerSingleton()}
 * 或 {@link Singleton @Singleton} 注解可以使其单例化
 * </p>
 *
 * <p>
 * 对于 {@link jakarta.inject.Provider} 来说, 如果其标注了 {@link Singleton @Singleton} 注解,
 * 则其 {@code get} 方法只会执行一次, 产生的对象会以单例一直存在, 不会在产生第二个对象
 * </p>
 */
public class SingletonModule extends AbstractModule {
    // 是否为单例的标记, 影响 SingletonBean 类型
    private final boolean singleton;

    /**
     * 构造一个是否使用单例的模块
     *
     * @param singleton 是否使用单例方式
     */
    public SingletonModule(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * 配置模块
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#asEagerSingleton()
     * AnnotatedBindingBuilder.asEagerSingleton()},
     * {@link com.google.inject.binder.LinkedBindingBuilder#asEagerSingleton()
     * LinkedBindingBuilder.asEagerSingleton()} 等类似方法, 用于令绑定关系成为单例模式
     * </p>
     */
    @Override
    protected void configure() {
        var builder1 = bind(SingletonBean.class)
            .annotatedWith(Names.named("Bean"))
            .to(SingletonBean.class);

        // 根据不同情况, 将 SingletonBean 绑定为单例或非单例
        if (singleton) {
            builder1.asEagerSingleton();
        }

        var builder2 = bind(SingletonBean.class)
            .annotatedWith(Names.named("Provider"))
            .toProvider(SingletonBean::new);

        // 根据不同情况, 将 Provider 绑定为单例或非单例
        if (singleton) {
            builder2.asEagerSingleton();
        }
    }
}
