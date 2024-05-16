package alvin.study.quarkus.web.interceptor;

import java.io.IOException;
import java.time.Instant;

import alvin.study.quarkus.web.endpoint.model.ErrorDto;
import io.quarkus.hibernate.validator.runtime.jaxrs.ViolationReport;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

/**
 * 定义写拦截器
 *
 * <p>
 * 写拦截器用于在"响应"写回客户端前调用, 可以对返回的数据进行处理
 * </p>
 */
@Provider
public class ResponseWrapperInterceptor implements WriterInterceptor {
    @Context
    HttpServerRequest request;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        var payload = context.getEntity();
        if (checkPayloadIfDto(payload)) {
            payload = resolveResponseEntity(payload);
            // 如果响应内容为一个 DTO 对象 (也包括 ErrorDto 对象), 则将其包装为 Response 对象返回
            context.setEntity(
                Response.builder()
                        .ok(!(payload instanceof ErrorDto))
                        .payload(payload)
                        .path(request.uri())
                        .timestamp(Instant.now())
                        .build());
        }

        // 处理下一个 WriteInterceptor 拦截器
        context.proceed();
    }

    /**
     * 将原本响应返回对象解析为 Response 对象
     *
     * @param payload 响应对象
     * @return 解析后的响应对象
     */
    private Object resolveResponseEntity(Object payload) {
        if (payload instanceof ViolationReport vr) {
            payload = ErrorDto.builder()
                    .status(vr.getStatus())
                    .message(vr.getTitle())
                    .violations(vr.getViolations())
                    .build();
        }

        return payload;
    }

    /**
     * 检查返回的响应对象是否为 DTO 对象
     *
     * @param payload 响应对象
     * @return 如果响应对象是一个 DTO 对象, 则返回 {@code true}, 否则返回 {@code false}
     */
    private static boolean checkPayloadIfDto(Object payload) {
        return !(payload instanceof String) &&
               !(payload instanceof Response) &&
               !(payload instanceof TemplateInstance);
    }
}
