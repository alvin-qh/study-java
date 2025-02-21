package alvin.study.guice.module.submodule;

import com.google.inject.AbstractModule;

import alvin.study.guice.module.bean.ModuleDemo;

/**
 * 定义子模块
 *
 * <p>
 * 在当前模块中, 指定了 {@link ModuleDemo} 类型和实例的绑定关系
 * </p>
 *
 * @see alvin.study.module.InstallSubModule
 */
public class SubModuleB extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * 子模块的概念和模块自身的定义没有关系, 所谓子模块也就是一个普通的 {@link com.google.inject.Module} 类型.
     * 另一个模块成为子模块, 是由于其是通过在父模块中通过
     * {@link com.google.inject.PrivateBinder#install(com.google.inject.Module)
     * PrivateBinder.install(Module)} 方法安装为子模块
     * </p>
     */
    @Override
    protected void configure() {
        bind(ModuleDemo.class).toInstance(new ModuleDemo("SubModuleB"));
    }
}
