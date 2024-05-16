package alvin.study.springboot.kickstart.core.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import graphql.ErrorType;

/**
 * 调用 API 时导致的异常类型
 */
public class ApiException extends GraphqlBaseException {
    // 错误扩展信息
    private final Map<String, Object> extensions = new LinkedHashMap<>();

    public ApiException(String message) {
        super(message, ErrorType.ValidationError);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause, ErrorType.ValidationError);
    }

    public ApiException addExtension(String key, Object value) {
        this.extensions.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> toExtensions() {
        var extensions = super.toExtensions();
        extensions.putAll(this.extensions);

        return extensions;
    }
}
