package alvin.study.quarkus.web.error;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.common.util.MediaTypeHelper;

import io.vertx.core.http.HttpServerRequest;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.util.ExceptionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.quarkus.util.StringUtil;
import alvin.study.quarkus.web.endpoint.model.ErrorDto;

/**
 * 自定义异常处理
 *
 * <p>
 * 对抛出的 {@link WebApplicationException} 异常进行特殊处理, 返回异常页面
 * </p>
 */
@Slf4j
@Provider
@RequiredArgsConstructor
public class WebApplicationExceptionMapper implements ExceptionMapper<Exception> {
    private static final List<MediaType> ERROR_MEDIA_TYPES = List.of(
        MediaType.TEXT_HTML_TYPE,
        MediaType.APPLICATION_JSON_TYPE,
        MediaType.TEXT_PLAIN_TYPE);

    // 从上下文中获取当前请求对象
    @Context
    HttpServerRequest request;

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
    public Response toResponse(Exception e) {
        // 将异常对象转为 Response 对象
        var resp = mapExceptionToResponse(e);

        // 解析请求中头携带的的 ACCEPT 值
        var mediaTypes = extractAccepts();
        // 检测响应类型
        var mediaType = determineMediaType(mediaTypes);

        // 创建错误对象
        var content = createErrorObject(mediaType, resp.getStatusInfo(), resp.getEntity().toString());

        // 产生新的 Response 对象
        return Response.fromResponse(resp)
                .type(mediaType)
                .entity(content)
                .build();
    }

    /**
     * 将异常转化为 {@link Response} 对象
     *
     * @param e 异常对象
     * @return 响应 {@link Response} 对象
     */
    private Response mapExceptionToResponse(Exception e) {
        if (e instanceof WebApplicationException we) {
            // 将异常堆栈信息转为字符串
            var sb = new StringBuilder(e.getMessage())
                    .append("\n\n")
                    .append(ExceptionUtil.generateStackTrace(e));

            return Response.fromResponse(we.getResponse())
                    .entity(sb.toString())
                    .build();
        }

        if (e instanceof IllegalArgumentException) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        log.error("Failed to process request to: {}", request.absoluteURI(), e);
        return Response.serverError().entity("Internal Server Error").build();
    }

    /**
     * 从请求的 Header 中解析 {@code ACCEPT} 头信息, 返回响应类型
     *
     * @return 根据请求 {@code ACCEPT} 头信息检测到的可能的相应类型列表
     */
    private List<MediaType> extractAccepts() {
        var acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (StringUtil.isNullOrEmpty(acceptHeader)) {
            return List.of();
        }

        // 解析 ACCEPT 头信息, 获取可能的相应类型
        return MediaTypeHelper.parseHeader(acceptHeader);
    }

    /**
     * 检测所需的 MediaType 类型
     *
     * @param acceptableMediaTypes 待检测的 MediaType 集合
     * @return 和所给 {@code acceptableMediaTypes} 参数最匹配的 MediaType 类型
     */
    private MediaType determineMediaType(List<MediaType> acceptableMediaTypes) {
        // 根据预设的响应类型列表, 从所给的响应类型中选取最合适的响应类型
        return MediaTypeHelper.getBestMatch(
            new ArrayList<>(ERROR_MEDIA_TYPES),
            new ArrayList<>(acceptableMediaTypes));
    }

    /**
     * 创建错误对象
     *
     * @param errorMediaType 相应类型
     * @param errorStatus    响应状态
     * @param errorDetails   错误信息
     * @return 表示错误的对象
     */
    private Object createErrorObject(MediaType errorMediaType, StatusType errorStatus, String errorDetails) {
        // 返回 JSON 类型的错误对象
        if (errorMediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return createJsonErrorResponse(errorStatus, errorDetails);
        }

        // 返回 HTML 类型的错误对象
        if (errorMediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return createHtmlErrorContent(errorStatus, errorDetails);
        }

        // 返回文本类型错误信息
        return createTextErrorContent(errorStatus, errorDetails);
    }

    /**
     * 创建 JSON 类型错误返回对象
     *
     * <p>
     * 本例中, JSON 类型错误是通过 {@link ErrorDto} 对象表示的, 所以本例中返回 {@link ErrorDto} 对象即可
     * </p>
     *
     * @param errorStatus  表示错误的状态码
     * @param errorDetails 错误详细信息
     * @return 表示错误的对象
     */
    private ErrorDto createJsonErrorResponse(StatusType errorStatus, String errorDetails) {
        return new ErrorDto(
            errorStatus.getStatusCode(),
            errorStatus.getReasonPhrase(),
            errorDetails);
    }

    /**
     * 创建 HTML 模板类型错误返回对象
     *
     * <p>
     * 本例中, HTML 模板类型错误是通过 {@link TemplateInstance} 对象表示的, 参见 {@link Templates} 模板类型
     * </p>
     *
     * @param errorStatus  表示错误的状态码
     * @param errorDetails 错误详细信息
     * @return 表示错误的对象
     */
    private TemplateInstance createHtmlErrorContent(Response.StatusType errorStatus, String errorDetails) {
        return Templates.error(
            new ErrorDto(
                errorStatus.getStatusCode(),
                errorStatus.getReasonPhrase(),
                errorDetails));
    }

    /**
     * 创建文本类型错误返回对象
     *
     * @param errorStatus  表示错误的状态码
     * @param errorDetails 错误详细信息
     * @return 表示错误的对象
     */
    private static String createTextErrorContent(Response.StatusType errorStatus, String errorDetails) {
        var errorText = new StringBuilder();
        errorText.append("Error ")
                .append(errorStatus.getStatusCode())
                .append(" (").append(errorStatus.getReasonPhrase()).append(")");

        if (errorDetails != null) {
            errorText.append("\n\n").append(errorDetails);
        }

        return errorText.toString();
    }
}
