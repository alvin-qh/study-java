package alvin.study.quarkus.web.i18n;

import org.jboss.resteasy.reactive.common.util.LocaleHelper;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.MessageBundles;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.http.HttpHeaders;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;

@RequestScoped
public class AppMessager {
    @Context
    HttpServerRequest request;

    private Localized.Literal cachedLoc = null;

    public AppMessage appMessage(String language) {
        return MessageBundles.get(AppMessage.class, Localized.Literal.of(language));
    }

    public AppMessage appMessage() {
        if (cachedLoc == null) {
            var lang = LocaleHelper.toLanguageString(
                LocaleHelper.extractLocale(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)));
            cachedLoc = Localized.Literal.of(lang);
        }
        return MessageBundles.get(AppMessage.class, cachedLoc);
    }
}
