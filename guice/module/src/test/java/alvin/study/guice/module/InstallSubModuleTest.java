package alvin.study.guice.module;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.google.inject.Guice;

import alvin.study.guice.module.bean.ModuleDemo;

/**
 * 测试 {@link InstallSubModule} 模块
 *
 * <p>
 * 实例化 {@link InstallSubModule} 时, 需要指定一个子模块名称, 不同的名称会安装不同的子模块
 * </p>
 */
class InstallSubModuleTest {
    /**
     * 要注入的对象
     *
     * <p>
     * 该对象在不同子模块中的绑定关系定义不同, 所以安装不同的子模块会导致注入的该字段也不相同
     * </p>
     */
    @Inject
    private ModuleDemo bean;

    /**
     * 以 {@code "SubModuleA"} 实例化模块
     *
     * <p>
     * 此时会安装 {@link alvin.study.module.submodule.SubModuleA SubModuleA} 模块
     * </p>
     */
    @Test
    void module_shouldInstallSubModuleA() {
        var injector = Guice.createInjector(new InstallSubModule("SubModuleA"));
        injector.injectMembers(this);

        // 测试安装了 SubModuleA 模块后的情况
        then(bean.getValue()).isEqualTo("SubModuleA");
    }

    /**
     * 以 {@code "SubModuleB"} 实例化模块
     *
     * <p>
     * 此时会安装 {@link alvin.study.module.submodule.SubModuleB SubModuleB} 模块
     * </p>
     */
    @Test
    void module_shouldModuleBInstalled() {
        var injector = Guice.createInjector(new InstallSubModule("SubModuleB"));
        injector.injectMembers(this);

        // 测试安装了 SubModuleB 模块后的情况
        then(bean.getValue()).isEqualTo("SubModuleB");
    }
}
