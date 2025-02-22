package alvin.study.springboot.mvc.core.http;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvin.study.springboot.mvc.app.api.advice.ApiResponseAdvice;
import alvin.study.springboot.mvc.util.http.Servlets;

/**
 * 返回对象的包装类
 *
 * <p>
 * Spring 对所有返回的结果都会包装为 {@link org.springframework.http.ResponseEntity
 * ResponseEntity} 类型对象, 包括了返回的状态码, header, body 等信息. String 并未约束返回 body 的具体格式
 * </p>
 *
 * <p>
 * {@link ResponseWrapper} 类型的作用是将所有的返回 body 格式统一, 以便于客户端统一处理, 处理后的结果统一为如下格式:
 * </p>
 *
 * <pre>
 * {
 *    "retCode": 0,
 *    "retMsg": "success",
 *    "payload": {...},
 *    "path": "/api/context",
 *    "timestamp": "2022-10-01T12:00:00.1234",
 * }
 * </pre>
 *
 * <p>
 * 实际返回的对象 JSON 包装在 {@link ResponseWrapper#payload} 字段中, 提供了两个方法完成包装:
 * {@link ResponseWrapper#success(Object)} 和 {@link ResponseWrapper#error(int, String)}
 * </p>
 *
 * @param retCode   响应代码
 * @param retMsg    响应信息
 * @param payload   响应数据载体
 * @param path      请求路径
 * @param timestamp 响应时间戳
 */
public record ResponseWrapper<T>(
        @JsonProperty("retCode") int retCode,
        @JsonProperty("retMsg") String retMsg,
        @JsonProperty("payload") T payload,
        @JsonProperty("path") String path,
        @JsonProperty("timestamp") Instant timestamp) {

    private static final int SUCCESS_CODE = 0;
    private static final String SUCCESS_MESSAGE = "success";

    @JsonCreator
    public ResponseWrapper {}

    /**
     * 包装正确的响应数据
     *
     * @param payload 响应对象中的数据载荷
     */
    public static <T> ResponseWrapper<T> success(T payload) {
        return new ResponseWrapper<>(
            SUCCESS_CODE, // 正确返回代码
            SUCCESS_MESSAGE, // 正确返回信息
            payload, // 返回数据
            Servlets.getHttpServletRequest().getRequestURI(), // 请求路径
            Instant.now() // 返回时间戳
        );
    }

    /**
     * 包装错误的响应数据
     */
    public static ResponseWrapper<Void> error(int code, String message) {
        return new ResponseWrapper<>(
            code, // 错误返回代码
            message, // 错误返回信息
            null, // 返回数据
            Servlets.getHttpServletRequest().getRequestURI(), // 请求路径
            Instant.now() // 返回时间戳
        );
    }

    /**
     * 包装错误的响应数据
     *
     * @param message     错误信息
     * @param errorDetail 错误详情
     */
    public static ResponseWrapper<ErrorDetail> error(int code, String message, ErrorDetail errorDetail) {
        return new ResponseWrapper<>(
            code, // 错误返回代码
            message, // 错误返回信息
            errorDetail, // 返回数据
            Servlets.getHttpServletRequest().getRequestURI(), // 请求路径
            Instant.now() // 返回时间戳
        );
    }

    /**
     * 包装标准的错误信息
     *
     * <p>
     * 一般情况, 错误都是有验证器验证结果提供的, 会以异常方式或者
     * {@link org.springframework.validation.BindingResult BindingResult} 对象形式返回,
     * 前者更容易处理, 参考: {@link ApiResponseAdvice ApiResponseAdvice} 类中的 {@code ApiResponseAdvice.handle(...)}
     * </p>
     *
     * <p>
     * 如果需要返回错误信息, 例如 {@code 400 BAD_REQUEST}, {@code 500 INTERNAL_ERROR} 等
     * </p>
     *
     * <p>
     * 错误详细信息包含:
     * <ol>
     * <li>
     * 请求中的 url 参数在转换为 Controller 方法参数时发生错误, 错误信息包含在
     * {@link ErrorDetail#errorParameters} 字段中
     * </li>
     * <li>
     * 请求中的 Body 在转换为对象时发生错误, 错误信息包含在 {@link ErrorDetail#errorFields} 字段中
     * </li>
     * </ol>
     * </p>
     *
     * <p>
     * 所以最终返回客户端的信息格式如下:
     * </p>
     *
     * <pre>
     * {
     *   "errorParameters": {
     *       "name": ["length must between 1 and 10"]
     *   },
     *   "errorFields": {
     *       "user.gender": ["value must in \"M\" or \"F\""]
     *   }
     * }
     * </pre>
     *
     * <p>
     * {@link ErrorDetail} 对象并不直接作为结果返回, 为了返回格式统一, 仍需通过 {@link ResponseWrapper}
     * 对象进行包装, 参考: {@link ResponseWrapper#error(int, String, ErrorDetail)} 方法
     * </p>
     *
     * @param errorParameters 错误参数列表
     * @param errorFields     错误字段列表
     */
    public record ErrorDetail(
            @JsonProperty("errorParameters") Map<String, String[]> errorParameters,
            @JsonProperty("errorFields") Map<String, String[]> errorFields) {

        @JsonCreator
        public ErrorDetail {}

        /**
         * 通过 {@code errorParameters} 字段创建 {@link ErrorDetail} 对象
         *
         * @param errorParameters 请求参数错误信息键值对
         * @return {@link ErrorDetail} 对象
         */
        public static ErrorDetail withErrorParameters(Map<String, String[]> errorParameters) {
            return new ErrorDetail(errorParameters, null);
        }

        /**
         * 通过 errorFields 字段创建 ErrorDetail 对象
         *
         * @param errorFields body 请求字段错误键值对
         * @return ErrorDetail 对象
         */
        public static ErrorDetail withErrorFields(Map<String, String[]> errorFields) {
            return new ErrorDetail(null, errorFields);
        }
    }
}
