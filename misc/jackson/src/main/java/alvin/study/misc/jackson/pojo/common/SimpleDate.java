package alvin.study.misc.jackson.pojo.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于测试 JSON 编码的 POJO 类型
 *
 * <p>
 * Jackson 通过反射将对象转为 JSON 字符串, 所以需要一个无参构造器实例化对象, 该构造器无需修饰为 {@code public}
 * </p>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class SimpleDate {
    private int year;
    private int month;
    private int day;
}
