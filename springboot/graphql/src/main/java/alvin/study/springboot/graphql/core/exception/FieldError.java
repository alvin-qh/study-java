package alvin.study.springboot.graphql.core.exception;

import jakarta.validation.ConstraintViolation;

import com.google.common.base.Joiner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 定义类用于表示一个字段解析时引发的错误信息
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class FieldError {
    // 错误字段名
    private String field;

    // 错误的字段值
    private Object rejectedValue;

    // 错误信息
    private String message;

    /**
     * 构造器, 只传入错误字段名与错误值
     *
     * @param field 错误字段名
     * @param rejectedValue 错误值
     */
    public FieldError(String field, Object rejectedValue) {
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    /**
     * 从 Spring 验证对象 ({@link ConstraintViolation}) 转化为 {@link FieldError} 对象
     *
     * @param violation Spring 验证错误对象
     * @return 错误字段对象
     */
    public static FieldError from(ConstraintViolation<?> violation) {
        var path = violation.getPropertyPath();
        var iter = path.iterator();
        if (iter.hasNext()) {
            iter.next();
        }
        var propertyName = Joiner.on(".").join(iter);
        return new FieldError(propertyName, violation.getInvalidValue(), violation.getMessage());
    }
}
