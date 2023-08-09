package alvin.study.quarkus.web.endpoint;

import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * 测试 Restful 请求
 */
@Path("restful")
public class RestfulResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    String applicationName;

    /**
     * 测试基本的 Restful 请求
     *
     * @param name 请求参数
     * @return 返回结果, 本例为字符串结果
     */
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("name") String name) {
        return "Hello " + Objects.requireNonNullElse(name, applicationName);
    }
}
