package alvin.study.quarkus.web.endpoint;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.extern.slf4j.Slf4j;

import alvin.study.quarkus.util.StringUtil;
import alvin.study.quarkus.web.endpoint.model.UserDto;
import alvin.study.quarkus.web.persist.entity.Gender;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

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
            // 通过模板对象的 data 方法, 传递键值对传递模板参数
            return uncheckedTemplate.data(
                "name", Objects.requireNonNullElse(name, applicationName),
                "gender", Gender.valueOf(gender),
                "birthday", StringUtil.emptyThenMapping(birthday, LocalDate::parse));
        } catch (IllegalArgumentException | DateTimeParseException e) {
            log.warn("Invalid query parameters", e);
            throw new WebApplicationException(e, 400);
        }
    }

    /**
     * 定义 Checked 模板类
     *
     * <p>
     * Checked 模板可以定义明确的渲染参数 (本例为 {@link UserDto} 类型对象), 对应的模板固定文件路径为
     * {@code resources:templates/<模板类所在类名>/<模板类方法名>.html}, 本例中为
     * {@code resources:templates/TemplatedResource/checkedTemplate.html} 文件
     * </p>
     */
    @CheckedTemplate
    static class Templates {
        /**
         * 定义模板渲染参数
         *
         * @param user 模板参数
         * @return 模板对象
         */
        static native TemplateInstance checkedTemplate(UserDto user);
    }

    /**
     * 演示对 {@code resources:templates/TemplatedResource/checkedTemplate.html} 模板进行渲染
     *
     * @param name     请求参数, 即请求 URL 中的 {@code ?name=?} 参数
     * @param gender   请求参数, 同上
     * @param birthday 请求参数, 同上
     *
     * @return 渲染模板的 {@link TemplateInstance} 对象
     */
    @GET
    @Path("checked")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance checked(
            @QueryParam("name") String name,
            @QueryParam("gender") @DefaultValue("MALE") String gender,
            @QueryParam("birthday") String birthday) {
        try {
            var user = UserDto.builder()
                    .name(Objects.requireNonNullElse(name, applicationName))
                    .gender(Gender.valueOf(gender))
                    .birthday(StringUtil.emptyThenMapping(birthday, LocalDate::parse))
                    .build();
            // 通过模板对象的渲染方法, 传递确定的对象类型渲染模板
            return Templates.checkedTemplate(user);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            log.warn("Invalid query parameters", e);
            throw new WebApplicationException(e, 400);
        }
    }
}
