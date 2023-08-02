package alvin.study.quarkus.web.endpoint;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 *
 */
@Path("/hello")
public class HelloResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    private String applicationName;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return applicationName;
    }
}
