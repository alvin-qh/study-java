package alvin.study.guice.inject;

import alvin.study.guice.inject.AssistedInjectModule.ConnectionFactory;
import com.google.inject.Module;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link AssistedInjectModule} 模块
 *
 * <p>
 * 确认辅助参数注入是否正常工作
 * </p>
 */
class AssistedInjectModuleTest extends BaseModuleTest {
    /**
     * 注入用于产生 {@link alvin.study.inject.AssistedInjectModule.Connection Connection}
     * 类型的工厂类实例
     */
    @Inject
    private ConnectionFactory factory;

    @Override
    protected Module getModule() {
        return new AssistedInjectModule();
    }

    /**
     * 验证通过工厂方法创建 {@link alvin.study.inject.AssistedInjectModule.Connection
     * Connection} 实例
     *
     * <p>
     * 调用 {@link ConnectionFactory#create(String, String)} 方法, 传入 {@code account} 和
     * {@code password} 参数, 确认 {@code url} 和 {@code timeout} 参数可以自行注入
     * </p>
     *
     * <p>
     * 调用 {@link ConnectionFactory#createAnonymous()} 方法, 无参数传入, 确认 {@code url} 和
     * {@code timeout} 参数可以自行注入
     * </p>
     */
    @Test
    void inject_shouldCreateConnectionObjectByFactory() {
        // 传入 account, password 两个参数, 创建 Connection 对象
        var connect = factory.create("alvin", "123456");
        then(connect).hasToString("alvin:123456@alvin.edu?timeout=1000");

        // 不传递参数, 创建 Connection 对象
        connect = factory.createAnonymous();
        then(connect).hasToString("anonymous@alvin.edu?timeout=1000");
    }
}
