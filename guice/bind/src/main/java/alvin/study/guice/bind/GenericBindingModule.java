package alvin.study.guice.bind;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import jakarta.inject.Singleton;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * 根据<b>泛型参数</b>匹配注入对象类型
 *
 * <p>
 * 对于一个确定类型, 可以直接将其绑定到确定的目标类型上, 但对于一个泛型类型, 不同的泛型参数意味着绑定的目标可能有所不同
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
public class GenericBindingModule extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * {@link AbstractModule#bind(TypeLiteral)} 用于绑定一个泛型类型, 返回一个
     * {@link com.google.inject.binder.AnnotatedBindingBuilder
     * AnnotatedBindingBuilder} 接口对象, 用于进一步配置类型绑定的目标
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#to(TypeLiteral)
     * AnnotatedBindingBuilder.to(TypeLiteral)} 方法表示将给定的泛型类型绑定到一个实际的泛型类型上, 返回一个
     * {@link com.google.inject.binder.ScopedBindingBuilder ScopedBindingBuilder}
     * 类型对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.ScopedBindingBuilder#asEagerSingleton()
     * ScopedBindingBuilder.asEagerSingleton} 表示最终注入的对象为单例模式对象
     * </p>
     *
     * <p>
     * {@link com.google.inject.binder.AnnotatedBindingBuilder#toInstance(Object)
     * AnnotatedBindingBuilder.toInstance(Object)} 方法表示将给定的泛型类型绑定到一个实际的对象上
     * </p>
     *
     * <p>
     * <code>bind(new TypeLiteral&lt;Set&lt;Integer&gt;&gt;() {}).to(new TypeLiteral&lt;HashSet&lt;Integer&gt;&gt;() {})</code>
     * 这段代码说明的是, 当注入具备泛型参数的 {@code Set<Integer>} 类型字段时, 实际会注入一个
     * {@code HashSet<Integer>} 类型对象
     * </p>
     *
     * <p>
     * <code>bind(new TypeLiteral&lt;Set&lt;Double&gt;&gt;() {}).toInstance(new TreeSet&lt;&gt;(asList(1.1, 1.2, 1.3, 1.4)))</code>
     * 这段代码说明的是, 当注入具备泛型参数的 {@code Set<Double>} 类型字段时, 实际会注入一个具体的
     * {@code TreeSet<Double>} 类型对象
     * </p>
     */
    @Override
    protected void configure() {
        // 为 Set<Integer> 泛型接口绑定类型
        bind(new TypeLiteral<Set<Integer>>() { })
            .to(new TypeLiteral<HashSet<Integer>>() { })
            .asEagerSingleton();

        // 为 Set<Double> 泛型接口绑定对象实例
        bind(new TypeLiteral<Set<Double>>() { })
            .toInstance(new TreeSet<>(asList(1.1, 1.2, 1.3, 1.4)));
    }

    /**
     * 为 Set<String> 泛型接口绑定 Provider
     *
     * <p>
     * 对于泛型绑定, 也可以通过 {@link Provides @Provides} 注解标注一个方法, 该方法的返回值即为要绑定的泛型类型,
     * 返回值即为绑定的目标对象
     * </p>
     */
    @Provides
    @Singleton
    public Set<String> provideSet() {
        return new LinkedHashSet<>(asList("a", "b", "c", "d"));
    }
}
