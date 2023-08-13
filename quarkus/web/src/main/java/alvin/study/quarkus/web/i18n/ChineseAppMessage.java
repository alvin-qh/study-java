package alvin.study.quarkus.web.i18n;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Message;

/**
 * 为 {@code zh} 语系定义 i18n 国际化类
 */
@Localized("zh")
public interface ChineseAppMessage extends AppMessage {
    /**
     * 定义 {@code zh} 语系的国际化信息
     */
    @Override
    @Message("你好 {name}")
    String hello(String name);

    // 无需为 numbers 方法进行覆盖, number 在 zh 语系的国际化信息在 resources:messages/msg_zh.properties 文件中定义
}
