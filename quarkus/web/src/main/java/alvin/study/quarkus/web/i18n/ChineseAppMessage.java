package alvin.study.quarkus.web.i18n;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Message;

@Localized("zh")
public interface ChineseAppMessage extends AppMessage {
    @Override
    @Message("你好 {name}")
    String hello(String name);
}
