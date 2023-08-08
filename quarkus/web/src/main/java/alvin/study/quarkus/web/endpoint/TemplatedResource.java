package alvin.study.quarkus.web.endpoint;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import alvin.study.quarkus.web.endpoint.model.Gender;
import alvin.study.quarkus.web.endpoint.model.UserDto;
import alvin.study.quarkus.web.util.ObjectUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * 演示 Quarkus 的后端渲染
 *
 * <p>
 * 如果引入了 {@code quarkus-resteasy-reactive-qute} 或 @{@code quarkus-resteasy-qute} 依赖,
 * 则可以在 HTTP 请求中使用"模板渲染", 即通过参数将模板文件渲染为目标 HTML 并返回前端浏览器
 * </p>
 *
 * <p>
 * 模板渲染又分为弱类型渲染和强类型渲染, 前者相当于通过一个 {@link Map} 对象通过键值对对模板内容进行渲染; 后者相当于对模板进行编译,
 * 对传入的参数有明确的类型要求
 * </p>
 */
@Slf4j
@Path("template")
public class TemplatedResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    String applicationName;

    /**
     * 注入 Unchecked 模板对象
     *
     * <p>
     * {@code @Location} 注解用于指定模板资源的位置, 默认为 {@code resources/templates/<字段名>}
     * </p>
     */
    @Inject
    @Location("common/uncheckedTemplate.txt")
    Template uncheckedTemplate;

    /**
     * 演示对 {@code common/uncheckedTemplate.txt} 模板进行渲染
     *
     * @param name     请求参数, 即请求 URL 中的 {@code ?name=?} 参数
     * @param gender   请求参数, 同上
     * @param birthday 请求参数, 同上
     *
     * @return 渲染模板的 {@link TemplateInstance} 对象
     */
    @GET
    @Path("unchecked")
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance unchecked(
            @QueryParam("name") String name,
            @QueryParam("gender") @DefaultValue("MALE") String gender,
            @QueryParam("birthday") String birthday) {
        if (StringUtil.isNullOrEmpty(name)) {
            name = applicationName;
        }
        try {
            return uncheckedTemplate.data(
                "name", Objects.requireNonNullElse(name, applicationName),
                "gender", Gender.valueOf(gender),
                "birthday", ObjectUtil.nullElse(birthday, () -> LocalDate.parse(birthday)));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            log.warn("Invalid query parameters", e);
            throw new WebApplicationException(e, 400);
        }
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance checkedTemplate(UserDto user);
    }

    @GET
    @Path("checked")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance checked(
            @QueryParam("name") String name,
            @QueryParam("gender") Gender gender,
            @QueryParam("birthday") LocalDate birthday) {
        var user = UserDto.builder()
                .name(Objects.requireNonNullElse(name, applicationName))
                .gender(gender)
                .birthday(birthday)
                .build();
        return Templates.checkedTemplate(user);
    }
}
