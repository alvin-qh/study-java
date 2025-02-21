package alvin.study.guice.module;

import com.google.inject.AbstractModule;

import alvin.study.guice.module.submodule.SubModuleA;
import alvin.study.guice.module.submodule.SubModuleB;

/**
 * 安装子模块
 *
 * <p>
 * 通过 {@link com.google.inject.PrivateBinder#install(com.google.inject.Module)
 * PrivateBinder.install(Module)} 方法可以在当前模块范围内安装一个子模块
 * </p>
 *
 * <p>
 * 通过安装不同的子模块, 即可在当期模块中引入不同的对象绑定关系
 * </p>
 */
public class InstallSubModule extends AbstractModule {
    // 要安装的子模块名称
    private final String moduleName;

    /**
     * 构造器, 给出当前模块要进一步安装的子模块名称
     *
     * @param moduleName 子模块名称字符串
     */
    public InstallSubModule(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * 配置模块
     *
     * <p>
     * 根据当前模块实例化时指定的子模块名称, 安装不同的子模块
     * </p>
     *
     * @see alvin.study.module.submodule.SubModuleA
     * @see alvin.study.module.submodule.SubModuleB
     */
    @Override
    protected void configure() {
        // 根据不同的名称, 装置不同的模块
        switch (moduleName) {
        case "SubModuleA" -> install(new SubModuleA());
        case "SubModuleB" -> install(new SubModuleB());
        default -> throw new IllegalArgumentException("Unknown module");
        }
    }
}
