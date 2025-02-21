package alvin.study.guice.inject;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;

import alvin.study.guice.inject.SimpleInjectModule.InjectorByConstruct;
import alvin.study.guice.inject.SimpleInjectModule.InjectorByField;
import alvin.study.guice.inject.SimpleInjectModule.InjectorBySetter;
import jakarta.inject.Inject;

/**
 * 测试基本的依赖注入
 */
class SimpleInjectModuleTest {
    // 通过字段注入
    @Inject
    private InjectorByField injectorByField;

    // 通过构造器注入
    @Inject
    private InjectorByConstruct injectorByConstruct;

    // 通过 setter 方法注入
    @Inject
    private InjectorBySetter injectorBySetter;

    @BeforeEach
    void beforeEach() {
        // 创建注入器, 并将所需内容注入当前对象
        Guice.createInjector(new SimpleInjectModule()).injectMembers(this);
    }

    /**
     * 确认注入的对象符合预期
     */
    @Test
    void inject_shouldBeanInjected() {
        then(injectorByField.getBean().getValue()).isEqualTo("inject_demo");
        then(injectorByConstruct.getBean().getValue()).isEqualTo("inject_demo");
        then(injectorBySetter.getBean().getValue()).isEqualTo("inject_demo");
    }
}
