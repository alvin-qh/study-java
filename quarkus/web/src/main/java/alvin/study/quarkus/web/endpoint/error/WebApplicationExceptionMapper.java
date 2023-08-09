package alvin.study.quarkus.web.endpoint.error;

import alvin.study.quarkus.web.endpoint.model.ErrorDto;
import io.netty.util.internal.ThrowableUtil;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * 自定义异常处理
 *
 * <p>
 * 对抛出的 {@link WebApplicationException} 异常进行特殊处理, 返回异常页面
 * </p>
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    /**
     * 异常页面对象
     *
     * <p>
     * 由 {@code resources:templates/WebApplicationExceptionMapper/error.html} 页面形成的模板对象
     * </p>
     */
    @CheckedTemplate
    static class Templates {
        /**
         * 模板渲染函数
         *
         * @param error 模板参数
         * @return 模板对象
         */
        static native TemplateInstance error(ErrorDto error);
    }

    @Override
    public Response toResponse(WebApplicationException e) {
        // 获取异常中的原始响应对象
        var rawResp = e.getResponse();

        // 通过模板形成新的响应对象
        return Response.ok(Templates.error(
            ErrorDto.builder()
                    .status(rawResp.getStatus())
                    .message(e.getMessage())
                    .detail(ThrowableUtil.stackTraceToString(e))
                    .build()))
                .status(rawResp.getStatus()).build();
    }
}
