package alvin.study.quarkus.web.endpoint.error;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.reactive.common.util.MediaTypeHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alvin.study.quarkus.util.StringUtil;
import alvin.study.quarkus.web.endpoint.model.ErrorDto;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final List<MediaType> ERROR_MEDIA_TYPES = List.of(
        MediaType.TEXT_PLAIN_TYPE,
        MediaType.TEXT_HTML_TYPE,
        MediaType.APPLICATION_JSON_TYPE);

    // 获取当前请求对象
    @Context
    HttpServerRequest request;

    // JSON 处理对象
    private final ObjectMapper objectMapper;

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
        var resp = mapExceptionToResponse(e);

        var mediaTypes = extractAccepts();
        var mediaType = determineErrorContentMediaType(mediaTypes);

        var content = createErrorContent(mediaType, resp.getStatusInfo(), resp.getEntity().toString());

        return Response.fromResponse(resp)
                .type(mediaType)
                .entity(content)
                .build();
    }

    private List<MediaType> extractAccepts() {
        var acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (StringUtil.isNullOrEmpty(acceptHeader)) {
            return List.of();
        }

        return MediaTypeHelper.parseHeader(acceptHeader);
    }

    private Response mapExceptionToResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            var originalErrorResponse = ((WebApplicationException) exception).getResponse();
            return Response.fromResponse(originalErrorResponse)
                    .entity(exception.getMessage())
                    .build();
        }

        if (exception instanceof IllegalArgumentException) {
            return Response.status(400).entity(exception.getMessage()).build();
        }

        log.error("Failed to process request to: {}", request.absoluteURI(), exception);
        return Response.serverError().entity("Internal Server Error").build();
    }

    private MediaType determineErrorContentMediaType(List<MediaType> acceptableMediaTypes) {
        return MediaTypeHelper.getBestMatch(new ArrayList<>(ERROR_MEDIA_TYPES), new ArrayList<>(acceptableMediaTypes));
    }

    private String createErrorContent(MediaType errorMediaType, Response.StatusType errorStatus, String errorDetails) {
        if (errorMediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return createJsonErrorContent(errorStatus, errorDetails);
        }

        if (errorMediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return createHtmlErrorContent(errorStatus, errorDetails);
        }

        return createTextErrorContent(errorStatus, errorDetails);
    }

    private String createJsonErrorContent(Response.StatusType errorStatus, String errorDetails) {
        var errNode = objectMapper.createObjectNode();
        errNode.put("status", errorStatus.getStatusCode());
        errNode.put("title", errorStatus.getReasonPhrase());

        if (errorDetails != null) {
            errNode.put("detail", errorDetails);
        }

        var errArray = objectMapper.createArrayNode().add(errNode);

        try {
            return objectMapper.writeValueAsString(errArray);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String createHtmlErrorContent(Response.StatusType errorStatus, String errorDetails) {
        return Templates.error(
            ErrorDto.builder()
                    .status(errorStatus.getStatusCode())
                    .detail(errorDetails)
                    .message(errorStatus.getReasonPhrase())
                    .build())
                .render();
    }

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
