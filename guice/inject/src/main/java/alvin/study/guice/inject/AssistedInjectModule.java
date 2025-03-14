package alvin.study.guice.inject;

import jakarta.inject.Named;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * 参数辅助注入
 *
 * <p>
 * 有些情况下需要手动传递部分参数, 自动注入另一部分参数, 此时可以通过参数辅助注入来完成
 * </p>
 *
 * <p>
 * 辅助注入包括:
 * <ul>
 * <li>
 * {@link AssistedInject @AssistedInject} 注解, 标记在构造器上,
 * 表示构造器的参数需进行辅助注入
 * </li>
 * <li>
 * {@link Assisted @Assisted} 注解, 标记在构造器参数上, 表示这个参数需要手动传递,
 * 其余未标记的参数会被自动注入
 * </li>
 * <li>
 * 使用辅助注入的类型无法直接通过 {@link jakarta.inject.Inject @Inject} 注解注入,
 * 必须通过一个工厂类进行构建
 * </li>
 * </ul>
 * </p>
 */
public class AssistedInjectModule extends AbstractModule {
    /**
     * 配置模块
     *
     * <p>
     * {@link AbstractModule#install(com.google.inject.Module)
     * AbstractModule.install(Module)} 安装一个子模块
     * </p>
     *
     * <p>
     * {@link FactoryModuleBuilder#build(Class)} 方法用于将一个工厂接口包装为一个
     * {@link com.google.inject.Module Module} 类型对象, 被包装的工厂类参考
     * {@link ConnectionFactory} 类型
     * </p>
     */
    @Override
    protected void configure() {
        // 安装工厂类模块 (通过 FactoryModuleBuilder.build 方法创建),
        // 指定通过那个工厂类型来创建工厂对象
        install(new FactoryModuleBuilder()
                .build(ConnectionFactory.class));

        /*
         * 如果需要将绑定绑定到类型, 则需进一步使用 .implement 方法指定接口和实现类的关系
         * install(new FactoryModuleBuilder()
         * .implement(Connection.class, ConnectionImpl.class)
         * .build(ConnectionFactory.class));
         */

        // 通过指定名称绑定一组值
        bind(String.class).annotatedWith(Names.named("url"))
                .toInstance("alvin.edu");

        bind(Integer.class).annotatedWith(Names.named("timeout"))
                .toInstance(1000);
    }

    /**
     * 参数辅助注入类型的工厂
     *
     * <p>
     * 对构造器方法具备 {@link AssistedInject @AssistedInject} 注解的类型,
     * 由于一部分参数需要注入, 所以无法直接通过 {@code new} 操作符创建实例,
     * 需要创建如下工厂接口, 并在 {@link AbstractModule#configure()} 方法中通过
     * {@code install(new FactoryModuleBuilder().build(ConnectionFactory.class))}
     * 安装为子模块
     * </p>
     */
    interface ConnectionFactory {
        /**
         * 创建 {@link Connection} 类型对象的工厂方法
         *
         * <p>
         * 工厂方法的方法名可以为任意名称, 如何匹配目标类型构造器构建对象是由工厂方法的参数来决定的
         * </p>
         *
         * <p>
         * 标记为 {@link Assisted @Assisted} 注解的参数为需要传递给 {@link Connection}
         * 类型构造器的参数, 其余构造器参数通过注入传递
         * </p>
         *
         * <p>
         * 该方法对应 {@link Connection#Connection(String, int, String, String)}
         * 构造器方法
         * </p>
         *
         * @param account  对应 {@link Connection} 类型的 {@code account} 参数
         * @param password 对应 {@link Connection} 类型的 {@code password} 参数
         */
        Connection create(
                @Assisted("account") String account,
                @Assisted("password") String password);

        /**
         * 创建 {@link Connection} 类型对象
         *
         * <p>
         * 工厂方法的方法名可以为任意名称, 如何匹配目标类型构造器构建对象是由工厂方法的参数来决定的
         * </p>
         *
         * <p>
         * 无参方法, 对应 {@link Connection#Connection(String, int)} 构造器方法,
         * 即类型无需手动传参的构造器 (均为自动注入参数或无参数)
         * </p>
         */
        Connection createAnonymous();
    }

    /**
     * 需要进行辅助注入的类
     */
    static class Connection {
        private final String url;
        private final int timeout;
        private final String account;
        private final String password;

        /**
         * 辅助注入构造器
         *
         * <p>
         * {@code url}, {@code timeout} 参数为注入
         * </p>
         *
         * <p>
         * {@code account}, {@code password} 参数为传参
         * </p>
         */
        @AssistedInject
        public Connection(
                @Named("url") String url,
                @Named("timeout") int timeout,
                @Assisted("account") String account,
                @Assisted("password") String password) {
            this.url = url;
            this.timeout = timeout;
            this.account = account;
            this.password = password;
        }

        /**
         * 即使没有需要自动注入的参数, {@link AssistedInject @AssistedInject} 注解也不能省略,
         * 否则工厂会报错
         */
        @AssistedInject
        public Connection(
                @Named("url") String url,
                @Named("timeout") int timeout) {
            this.url = url;
            this.timeout = timeout;
            this.account = null;
            this.password = null;
        }

        @Override
        public String toString() {
            if (Strings.isNullOrEmpty(account)) {
                return String.format("anonymous@%s?timeout=%s", url, timeout);
            }
            return String.format("%s:%s@%s?timeout=%s", account, password, url, timeout);
        }
    }
}
