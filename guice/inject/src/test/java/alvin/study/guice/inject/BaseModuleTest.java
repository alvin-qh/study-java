package alvin.study.guice.inject;

import org.junit.jupiter.api.BeforeEach;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 测试超类
 */
abstract class BaseModuleTest {
    // 注入器对象
    protected Injector injector;

    @BeforeEach
    void beforeEach() {
        // 获取注入器对象
        injector = Guice.createInjector(getModule());
        // 向当前对象注入成员字段
        injector.injectMembers(this);
    }

    /**
     * 获取模块对象
     *
     * @return 模块对象
     */
    protected abstract Module getModule();
}
