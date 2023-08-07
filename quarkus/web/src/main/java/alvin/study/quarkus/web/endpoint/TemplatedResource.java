package alvin.study.quarkus.web.endpoint;

import java.util.Map;

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
@Path("template")
public class TemplatedResource {
    // 通过 @ConfigProperty 注解可以注入配置文件中的配置项值
    @ConfigProperty(name = "application.name", defaultValue = "Hello from RESTEasy Reactive")
    private String applicationName;

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
     * 演示对 Unchecked 模板进行渲染
     *
     * @param name 请求参数, 即请求 URL 中的 {@code ?name=?} 参数
     * @return 渲染模板 {@link TemplateInstance} 对象
     */
    @GET
    @Path("unchecked")
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance unchecked(@QueryParam("name") String name) {
        if (StringUtil.isNullOrEmpty(name)) {
            name = applicationName;
        }
        return uncheckedTemplate.data("name", name);
    }

    @CheckedTemplate
    public static class SimpleTemplates {
        public static native TemplateInstance checkedTemplate(String name);
    }
}
