package alvin.study.springboot.graphql.core.exception;

/**
 * 错误主代码常量群
 */
public interface ErrorCode {
    String INPUT_ERROR = "input_error";
    String INTERNAL_ERROR = "internal_error";
    String UNAUTHORIZED = "unauthorized";
    String FORBIDDEN = "forbidden";
    String NOT_FOUND = "not_found";
    String REMOTE_ERROR = "remote_error";
    String DUPLICATED_KEY = "duplicated_entity_key";
}
