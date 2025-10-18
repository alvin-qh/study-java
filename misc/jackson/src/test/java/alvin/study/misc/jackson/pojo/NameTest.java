package alvin.study.misc.jackson.pojo;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;

/**
 * 测试 {@link Name} 类型的 JSON 序列化以及反序列化操作
 *
 * <p>
 * {@link Name} 类型标记了 {@link tools.jackson.annotation.JsonFilter @JsonFilter} 注解,
 * 表示该类型对象在序列化为 JSON 时, 可以通过过滤器控制输出的字段, 本例演示了如何定义过滤器,
 * 以及如何通过过滤器控制 JSON 输出字段
 * </p>
 */
class NameTest {
    // 实例化测试对象
    private static final Name OBJECT = new Name("", "Alvin", "Qu", "·");

    // 定义未通过过滤器时期待的 JSON 字符串
    private static final String JSON_NO_FILTER
        = "{\"fullName\":\"Alvin·Qu\",\"firstName\":\"Alvin\",\"lastName\":\"Qu\",\"middot\":\"·\"}";

    // 定义通过过滤器时期待的 JSON 字符串
    private static final String JSON_FILTERED = "{\"fullName\":\"Alvin·Qu\"}";

    /**
     * 测试通过
     * {@link tools.jackson.databind.json.JsonMapper.Builder#filterProvider(tools.jackson.databind.ser.FilterProvider)
     * JsonMapper.Builder.filterProvider(FilterProvider)} 方法设置过滤器
     *
     * <p>
     * Jackson 提供了 {@link SimpleFilterProvider} 类作为基本的过滤器提供器, 该类型实现了
     * {@link tools.jackson.databind.ser.FilterProvider FilterProvider} 接口
     * </p>
     *
     * <p>
     * {@link SimpleFilterProvider#addFilter(String, SimpleBeanPropertyFilter)} 方法用于添加一个过滤器
     * <ul>
     * <li>
     * 第一个参数为过滤器的名称, 必须和 {@link Name} 类型中 {@link tools.jackson.annotation.JsonFilter @JsonFilter}
     * 注解中定义的名称一致过滤器方可生效
     * </li>
     * <li>
     * 第二个参数为所需的过滤器对象, 过滤器对象为 {@link SimpleBeanPropertyFilter} 类型对象, 用于设置所需过滤的对象属性名称
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 本例中将要过滤的对象属性设置为空, 表示过滤器不进行任何过滤操作, 因此最终输出的 JSON 字符串应和未使用过滤器时相同
     * </p>
     */
    @Test
    @SneakyThrows
    void toJson_shouldEncodeObjectToJsonWithNonFilter() {
        // 产生一个基本过滤器 Provider 对象
        var provider = new SimpleFilterProvider()
                .addFilter(
                    "non-names",
                    SimpleBeanPropertyFilter.serializeAllExcept(Set.of()) // 添加一个过滤器
                );

        // 创建 ObjectMapper 对象
        var mapper = JsonMapper.builder()
                .filterProvider(provider)
                .build();

        // 将对象编码为 JSON 字符串, 确认结果正确
        var json = mapper.writeValueAsString(OBJECT);
        then(json).isEqualTo(JSON_NO_FILTER);
    }

    /**
     * 测试通过
     * {@link tools.jackson.databind.json.JsonMapper.Builder#filterProvider(tools.jackson.databind.ser.FilterProvider)
     * JsonMapper.Builder.filterProvider(FilterProvider)} 方法设置过滤器
     *
     * <p>
     * Jackson 提供了 {@link SimpleFilterProvider} 类作为基本的过滤器提供器, 该类型实现了
     * {@link tools.jackson.databind.ser.FilterProvider FilterProvider} 接口
     * </p>
     *
     * <p>
     * {@link SimpleFilterProvider#addFilter(String, SimpleBeanPropertyFilter)} 方法用于添加一个过滤器
     * <ul>
     * <li>
     * 第一个参数为过滤器的名称, 必须和 {@link Name} 类型中 {@link tools.jackson.annotation.JsonFilter @JsonFilter}
     * 注解中定义的名称一致过滤器方可生效
     * </li>
     * <li>
     * 第二个参数为所需的过滤器对象, 过滤器对象为 {@link SimpleBeanPropertyFilter} 类型对象, 用于设置所需过滤的对象属性名称
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 本例中设置了要过滤的对象属性名称, 因此输出的 JSON 字符串应不包含这些属性
     * </p>
     */
    @Test
    void toJson_shouldEncodeObjectToJsonWithPropertiesFilter() throws Exception {
        // 产生一个基本过滤器 Provider 对象
        var provider = new SimpleFilterProvider()
                .addFilter(
                    "non-names",
                    SimpleBeanPropertyFilter.serializeAllExcept(
                        Set.of("firstName", "lastName", "middot")) // 添加指定字符串过滤器
                );

        // 创建 ObjectMapper 对象
        var mapper = JsonMapper.builder()
                .filterProvider(provider)
                .build();

        // 将对象编码为 JSON 字符串, 确认结果正确
        var json = mapper.writeValueAsString(OBJECT);
        then(json).isEqualTo(JSON_FILTERED);
    }
}
