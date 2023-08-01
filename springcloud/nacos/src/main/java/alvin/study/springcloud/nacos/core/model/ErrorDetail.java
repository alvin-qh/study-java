package alvin.study.springcloud.nacos.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

/**
 * 包装标准的错误信息
 *
 * <p>
 * 一般情况, 错误都是有验证器验证结果提供的, 会以异常方式或者
 * {@link org.springframework.validation.BindingResult BindingResult} 对象形式返回,
 * 前者更容易处理, 参考: {@link alvin.study.app.api.advice.ApiResponseAdvice
 * ApiResponseAdvice} 类中的
 * {@link alvin.study.app.api.advice.ApiResponseAdvice#handleException(javax.validation.ConstraintViolationException)
 * ApiResponseAdvice.handleException(ConstraintViolationException)},
 * {@link alvin.study.app.api.advice.ApiResponseAdvice#handleException(org.springframework.web.bind.MethodArgumentNotValidException)
 * ApiResponseAdvice.handleException(MethodArgumentNotValidException)} 以及
 * {@link alvin.study.app.api.advice.ApiResponseAdvice#handleException(org.springframework.web.bind.MissingServletRequestParameterException)
 * ApiResponseAdvice.handleException(MissingServletRequestParameterException)}
 * 方法, 均是对当传递到 Controller 方法的参数不符合要求时抛出异常的处理方法
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
 * 对象进行包装, 参考: {@link ResponseWrapper#error(int, String, Object)} 方法
 * </p>
 */
@Getter
public class ErrorDetail {
    /**
     * 错误参数列表
     */
    private final Map<String, String[]> errorParameters;

    /**
     * 错误字段列表
     */
    private final Map<String, String[]> errorFields;

    /**
     * 构造器
     *
     * @param errorParameters 请求参数错误列表
     * @param errorFields     请求对象错误字段列表
     */
    @JsonCreator
    ErrorDetail(
        @JsonProperty("errorParameters") Map<String, String[]> errorParameters,
        @JsonProperty("errorFields") Map<String, String[]> errorFields) {
        this.errorParameters = errorParameters;
        this.errorFields = errorFields;
    }

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
