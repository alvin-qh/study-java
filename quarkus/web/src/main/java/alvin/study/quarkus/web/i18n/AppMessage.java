package alvin.study.quarkus.web.i18n;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

@MessageBundle
public interface AppMessage {
    @Message("Hello {name}")
    String hello(String name);
}
