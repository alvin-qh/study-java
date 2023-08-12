package alvin.study.quarkus.web.endpoint;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import alvin.study.quarkus.web.endpoint.model.Gender;
import alvin.study.quarkus.web.endpoint.model.UserDto;
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

    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDto> users() {
        return List.of(
            UserDto.builder()
                    .name("Alvin")
                    .gender(Gender.MALE)
                    .birthday(LocalDate.of(1981, 3, 17))
                    .build(),
            UserDto.builder()
                    .name("Emma")
                    .gender(Gender.FEMALE)
                    .birthday(LocalDate.of(1985, 3, 29))
                    .build());
    }
}
