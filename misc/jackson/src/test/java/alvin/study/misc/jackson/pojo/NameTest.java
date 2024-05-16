package alvin.study.misc.jackson.pojo;

import alvin.study.misc.jackson.encode.Encoder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Name} 类型的 JSON 序列化以及反序列化操作
 *
 * <p>
 * 本例演示了通过 {@link Encoder#addFilter(String, String...)} 方法设置对象属性过滤器
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
     * 测试 {@link Encoder#toJson(Object)} 方法, 通过一个未设置属性名的过滤器进行 JSON 编码
     */
    @Test
    void toJson_shouldEncodeObjectToJsonWithNonFilter() throws Exception {
        var enc = new Encoder(false);
        // 添加一个无字段限制的空过滤器
        enc.addFilter("non-names");

        // 将对象编码为 JSON 字符串, 确认结果正确
        var json = enc.toJson(OBJECT);
        then(json).isEqualTo(JSON_NO_FILTER);
    }

    /**
     * 测试 {@link Encoder#toJson(Object)} 方法, 通过一个设置指定属性名的过滤器进行 JSON 编码
     */
    @Test
    void toJson_shouldEncodeObjectToJsonWithPropertiesFilter() throws Exception {
        var enc = new Encoder(false);
        // 添加一个无字段限制的空过滤器
        enc.addFilter("non-names", "firstName", "lastName", "middot");

        // 将对象编码为 JSON 字符串, 确认结果正确
        var json = enc.toJson(OBJECT);
        then(json).isEqualTo(JSON_FILTERED);
    }
}
