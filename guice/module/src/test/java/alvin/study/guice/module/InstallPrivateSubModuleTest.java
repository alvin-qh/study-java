package alvin.study.guice.module;

import alvin.study.guice.module.bean.ModuleDemo;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

class InstallPrivateSubModuleTest extends BaseModuleTest {
    /**
     * 注入名为 {@code "Exposed"} 的 Bean 对象, 该绑定关系在私有子模块中暴露, 所以可以成功注入
     */
    @Inject
    @Named("Exposed")
    private ModuleDemo exposedBean;

    /**
     * 注入名为 {@code "Hidden"} 的 Bean 对象, 该绑定关系在私有子模块中暴露, 所以无法注入
     */
    // @Inject
    // @Named("Hidden")
    // private ModuleDemo hiddenBean;
    @Override
    protected Module getModule() {
        return new InstallPrivateSubModule();
    }

    /**
     * 确认 {@code exposedBean} 字段注入正确
     */
    @Test
    void module_shouldExposedBindingInjected() {
        then(exposedBean.getValue()).isEqualTo("Exposed Object");
    }

    /**
     * 确认 {@code hiddenBean} 字段确实无法注入
     */
    @Test
    void module_shouldPrivateModuleInstalled() {
        // 定义名为 Hidden 的 Key 对象
        var key = Key.get(ModuleDemo.class, Names.named("Hidden"));

        // 从 Bean 容器中获取名为 "Hidden" 的对象时, 会抛出异常
        thenThrownBy(() -> injector.getInstance(key)).isInstanceOf(ConfigurationException.class);
    }
}
