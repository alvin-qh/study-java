package alvin.study.quarkus.web.i18n;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

/**
 * 定义 i18n 国际化信息类
 */
@MessageBundle
public interface AppMessage {
    /**
     * 通过注解指定国际化信息
     *
     * <p>
     * 通过 {@link Message @Message} 注解指定国际化信息, <code>{name}</code> 为变量占位符
     * </p>
     *
     * @param name 参数
     * @return 格式化后的国际化信息
     */
    @Message("Hello {name}")
    String hello(String name);

    /**
     * 通过国际化文件指定国际化信息
     *
     * <p>
     * Quarkus 的国际化文件存储在 {@code resources:messages} 路径下, 以 {@code msg_*.properties} 命名, 默认语系为
     * {@code msg.properties} 文件, 其它语系包括 {@code msg_zh.properties} 等
     * </p>
     *
     * </p>
     * 参考国际化文件中的 {@code numbers} 参数设置
     * </p>
     *
     * @return 国际化信息
     */
    String numbers();
}
