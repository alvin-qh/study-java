package alvin.study.guice.module.submodule;

import alvin.study.guice.module.bean.ModuleDemo;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

/**
 * 私有子模块
 *
 * <p>
 * 私有模块有一个"暴露"的概念, 即安装了此模块后, 只能获取 (或注入) 被明确被暴露的实例
 * </p>
 *
 * <p>
 * 私有模块的主要作用是避免类型或名称污染. 例如一个多数据源项目, 则可以再各自的私有模块中配置各自的 {@code DataSource},
 * 然后暴漏出不同的 {@code EntityManager} 即可
 * </p>
 *
 * <p>
 * 私有模块需要继承自 {@link PrivateModule} 类
 * </p>
 */
public class SubPrivateModule extends PrivateModule {
    /**
     * 配置模块
     *
     * <p>
     * {@link com.google.inject.PrivateBinder#expose(Class)} 方法表示要暴露的绑定关系定义,
     * 其余的绑定关系在当前模块之外无法使用
     * </p>
     *
     * <p>
     * 本例中定义了 {@link ModuleDemo} 类型的两个绑定关系, 但只暴露了其中的一个, 所以通过父模块, 只能使用暴露的这个绑定关系
     * </p>
     */
    @Override
    protected void configure() {
        // 创建 Hidden 绑定关系
        bind(ModuleDemo.class)
            .annotatedWith(Names.named("Hidden"))
            .toInstance(new ModuleDemo("Hidden Object"));

        // 创建 Exposed 绑定关系
        bind(ModuleDemo.class)
            .annotatedWith(Names.named("Exposed"))
            .toInstance(new ModuleDemo("Exposed Object"));

        // 暴露 Exposed 绑定关系, 即 Hidden 绑定关系被隐藏
        expose(ModuleDemo.class).annotatedWith(Names.named("Exposed"));
    }
}
