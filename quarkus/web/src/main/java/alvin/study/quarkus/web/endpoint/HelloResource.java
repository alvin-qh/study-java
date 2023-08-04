package alvin.study.quarkus.web.endpoint;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 *
 */
@Path("hello")
public class HelloResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    private String applicationName;

    @Inject
    @Location("common/hello.txt")
    Template helloTemplate;

    @GET
    @Path("simple")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloSimple() {
        return "Hello " + applicationName;
    }

    @GET
    @Path("template")
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance helloTemplate(@QueryParam("name") String name) {
        if (StringUtil.isNullOrEmpty(name)) {
            name = applicationName;
        }
        return helloTemplate.data("name", name);
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance hello(String name);
    }

    @GET
    @Path("checked-template")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance helloCheckedTemplate(@QueryParam("name") String name) {
        if (StringUtil.isNullOrEmpty(name)) {
            name = applicationName;
        }
        return Templates.hello(name);
    }
}
