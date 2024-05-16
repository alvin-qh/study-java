package alvin.study.springboot.kickstart.core.exception;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;

/**
 * 表示一个错误字段的类型
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
