package alvin.study.springboot.spel.infra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于测试 SpEL 获取对 {@link org.springframework.context.ApplicationContext ApplicationContext} 中管理的 Bean 对象
 *
 * <p>
 * 参考 {@code SpelConfig.intValue()}
 * </p>
 *
 * @param <T> 值类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value<T> {
    private T value;
}
