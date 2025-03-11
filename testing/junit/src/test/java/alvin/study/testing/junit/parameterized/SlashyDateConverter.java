package alvin.study.testing.junit.parameterized;

import java.time.LocalDate;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

/**
 * 用于将测试提供的假设值从字符串类型转为 {@link LocalDate} 类型
 *
 * <p>
 * 字符串的格式为 {@code YEAR/MONTH/DATE}
 * </p>
 *
 * <p>
 * 转换需实现 {@link ArgumentConverter} 接口, 且在测试参数中通过
 * {@link org.junit.jupiter.params.converter.ConvertWith @ConvertWith}
 * 注解来指定转换类型
 * </p>
 */
public class SlashyDateConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        // 确保被转换类型必须为字符串
        if (!(source instanceof String s)) {
            throw new IllegalArgumentException("The argument should be a string: " + source);
        }

        try {
            // 将字符串通过 / 分割为若干部分
            var parts = s.split("/", 3);

            // 获取分隔的各个部分, 表示年月日
            var year = Integer.parseInt(parts[0]);
            var month = Integer.parseInt(parts[1]);
            var day = Integer.parseInt(parts[2]);

            // 返回转换结果
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert", e);
        }
    }
}
