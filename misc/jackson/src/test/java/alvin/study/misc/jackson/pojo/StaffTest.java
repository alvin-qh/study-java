package alvin.study.misc.jackson.pojo;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.jupiter.api.Test;

import alvin.study.misc.jackson.pojo.common.SimpleDate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * 测试 {@link Staff} 类型的 JSON 序列化和反序列化
 *
 * <p>
 * 在 {@link Staff} 类型的 {@code name} 属性上标记了
 * {@link com.fasterxml.jackson.annotation.JsonProperty @JsonProperty} 注解,
 * 用于指定对象属性和 JSON 字段的映射名称
 * </p>
 *
 * <p>
 * 在 {@link Staff} 类型的 {@code valid} 属性上标记了
 * {@link com.fasterxml.jackson.annotation.JsonIgnore @JsonIgnore} 注解,
 * 表示该对象类型不参与 JSON 序列化和反序列化
 * </p>
 *
 * <p>
 * {@link Staff#Staff()} 构造器用于 JSON 反序列化时, 构造对象
 * </p>
 */
class StaffTest {
    // 要序列化的对象
    private static final Staff OBJECT = new Staff(
        1L,
        "Alvin",
        "M",
        new SimpleDate(1981, 3, 17),
        false);

    // 期待的单个对象序列化结果
    private static final String SINGLE_JSON = """
        {
          "id" : 1,
          "staffName" : "Alvin",
          "gender" : "M",
          "birthday" : {
            "year" : 1981,
            "month" : 3,
            "day" : 17
          }
        }""";

    // 期待的集合对象序列化结果
    private static final String LIST_JSON = """
        [ {
          "id" : 1,
          "staffName" : "Alvin",
          "gender" : "M",
          "birthday" : {
            "year" : 1981,
            "month" : 3,
            "day" : 17
          }
        } ]""";

    /**
     * 测试将 {@link Staff} 类型对象序列化为 JSON 字符串
     *
     * <p>
     * 本例为将单个对象序列化为 JSON 字符串
     * </p>
     */
    @Test
    void toJson_shouldEncodeProductObjectToJson() {
        var mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = mapper.writeValueAsString(OBJECT);
        then(json).isEqualTo(SINGLE_JSON);
    }

    /**
     * 测试将集合对象序列化为 JSON 字符串
     *
     * <p>
     * 将 {@link List} 类型集合对象序列化为 JSON 字符串
     * </p>
     */
    @Test
    void toJson_shouldEncodeProductListToJson() {
        var mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = mapper.writeValueAsString(List.of(OBJECT));
        then(json).isEqualTo(LIST_JSON);
    }

    /**
     * 测试从 JSON 字符串反序列化为 {@link Staff} 类型对象
     *
     * <p>
     * 将 JSON 字符串转为单个对象
     * </p>
     *
     * <p>
     * 由于 {@link Staff} 类型具备一个 {@link Staff#Staff(Long, String, String, SimpleDate, boolean)}
     * 构造器, 故 Jackson 会自动匹配该构造器来创建对象, 而无需显式增加
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解
     * </p>
     *
     * <p>
     * 通过 {@link DeserializationFeature#FAIL_ON_NULL_FOR_PRIMITIVES} 配置项,
     * 置为 false, 忽略 JSON 中值为 null 的字段, 避免将 null 值赋给 Java 原始类型字段时抛出异常
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductObject() {
        var mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var obj = mapper.readValue(SINGLE_JSON, Staff.class);
        then(obj).isEqualTo(OBJECT);
    }

    /**
     * 测试从 JSON 字符串反序列化为集合对象
     *
     * <p>
     * 将 JSON 集合字符串转为 {@link List} 类型集合对象
     * </p>
     *
     * <p>
     * 由于 {@link Staff} 类型具备一个 {@link Staff#Staff(Long, String, String, SimpleDate, boolean)}
     * 构造器, 故 Jackson 会自动匹配该构造器来创建对象, 而无需显式增加
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解 (对于 Jackson 3.x 版本及以上)
     * </p>
     *
     * <p>
     * 通过 {@link DeserializationFeature#FAIL_ON_NULL_FOR_PRIMITIVES} 配置项,
     * 置为 false, 忽略 JSON 中值为 null 的字段, 避免将 null 值赋给 Java 原始类型字段时抛出异常
     * </p>
     *
     * <p>
     * 本次反序列化的类型包含泛型类型, 需通过 {@link TypeReference} 类型传递泛型类型参数
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductListByTypeReferenceArgument() {
        var mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var obj = mapper.readValue(LIST_JSON, new TypeReference<List<Staff>>() {});
        then(obj).isEqualTo(List.of(OBJECT));
    }
}
