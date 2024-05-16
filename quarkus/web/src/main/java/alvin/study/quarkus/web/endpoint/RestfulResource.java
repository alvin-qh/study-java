package alvin.study.quarkus.web.endpoint;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;

import alvin.study.quarkus.web.endpoint.model.UserDto;
import alvin.study.quarkus.web.i18n.AppMessager;
import alvin.study.quarkus.web.persist.DataSource;
import alvin.study.quarkus.web.persist.entity.Gender;
import alvin.study.quarkus.web.persist.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

/**
 * 测试 Restful 请求
 */
@Path("restful")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class RestfulResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    String applicationName;

    // 注入数据源对象
    private final DataSource dataSource;

    // 注入本地化信息对象
    private final AppMessager messager;

    /**
     * 演示基本的 Restful 请求
     *
     * @param name 请求参数
     * @return 返回结果, 本例为字符串结果
     */
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("name") String name) {
        var message = messager.appMessage();
        return message.hello(Objects.requireNonNullElse(name, applicationName));
    }

    @GET
    @Path("numbers")
    @Produces(MediaType.TEXT_PLAIN)
    public String numbers(@QueryParam("digit") @Min(0) @Max(10) int number) {
        var message = messager.appMessage();
        return message.numbers().split(",")[number];
    }

    /**
     * 演示通过 RESTful 请求返回 JSON 对象
     *
     * @return {@link UserDto} 对象集合
     */
    @GET
    @Path("users")
    public List<UserDto> users() {
        return List.of(
            UserDto.builder()
                    .id("001")
                    .name("Alvin")
                    .gender(Gender.MALE)
                    .birthday(LocalDate.of(1981, 3, 17))
                    .build(),
            UserDto.builder()
                    .id("002")
                    .name("Emma")
                    .gender(Gender.FEMALE)
                    .birthday(LocalDate.of(1985, 3, 29))
                    .build());
    }

    /**
     * 演示通过 RESTful 请求创建实体
     *
     * @return {@link UserDto} 对象集合
     */
    @POST
    @Path("users")
    @ResponseStatus(201)
    public UserDto createUser(@Valid @NotNull UserDto user) {
        // 生成实体 id
        var id = UUID.randomUUID().toString();

        // 创建实体对象
        var entity = User.builder()
                .id(id)
                .name(user.name())
                .birthday(user.birthday())
                .gender(user.gender())
                .build();

        // 存储实体对象
        dataSource.save(id, entity);

        // 返回结果
        return UserDto.builder()
                .id(entity.id())
                .name(entity.name())
                .birthday(entity.birthday())
                .gender(entity.gender())
                .build();
    }
}
