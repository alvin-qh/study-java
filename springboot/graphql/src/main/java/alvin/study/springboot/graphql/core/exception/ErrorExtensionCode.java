package alvin.study.springboot.graphql.core.exception;

/**
 * 扩展错误代码常量群
 */
public interface ErrorExtensionCode {
    String REASON = "reason";
    String ERROR_FIELDS = "errorFields";
    String REMOTE_ERROR = "remoteError";
    String DUPLICATED_KEYS = "duplicatedKeys";
    String MISSING_KEYS = "missingKeys";
    String CLIENT_ERROR = "clientError";
}
