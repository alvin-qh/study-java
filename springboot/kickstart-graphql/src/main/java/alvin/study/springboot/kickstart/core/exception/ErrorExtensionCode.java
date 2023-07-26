package alvin.study.springboot.kickstart.core.exception;

/**
 * 扩展错误代码
 */
public interface ErrorExtensionCode {
    String REASON = "reason";
    String ERROR_FIELDS = "errorFields";
    String REMOTE_ERROR = "remoteError";
    String DUPLICATED_KEYS = "duplicatedKeys";
    String MISSING_KEYS = "missingKeys";
    String CLIENT_ERROR = "clientError";
}
