package alvin.study.guice.module;

import com.google.inject.AbstractModule;

import alvin.study.guice.module.submodule.SubPrivateModule;

/**
 * 安装私有子模块
 *
 * <p>
 * 安装私有子模块 {@link com.google.inject.PrivateModule PrivateModule} 定义了一种特殊的模块,
 * 该模块可以指定需要"暴露"给外部的绑定关系, 隐藏未暴露的绑定关系
 * </p>
 *
 * <p>
 * 如此以来, 可以避免多个子模块同时引入对绑定关系造成的"污染", 即相同名称, 类型的绑定关系相互影响
 * </p>
 */
public class InstallPrivateSubModule extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * 安装私有子模块和安装普通模块的方法一致, 区别仅在于子模块的定义
     * </p>
     */
    @Override
    protected void configure() {
        install(new SubPrivateModule());
    }
}
