package alvin.study.misc.jackson.pojo;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

/**
 * 测试 {@link Person} 类型的 JSON 序列化以及反序列化操作
 *
 * <p>
 * 本测试演示了通过 {@link com.fasterxml.jackson.annotation.JsonIgnoreProperties @JsonIgnoreProperties}
 * 注解忽略指定的对象属性, 不包含在序列化结果中
 * </p>
 *
 * <p>
 * 本测试演示通过 {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator}
 * 注解来完成 JSON 到对象的反序列化
 * </p>
 */
class PersonTest {
    // 要序列化的对象
    private static final Person OBJECT = new Person(
        1L,
        "Alvin.Qu",
        "M",
        LocalDate.of(1981, 3, 17));

    // 期待的序列化结果
    private static final String JSON = "{\"id\":1,\"name\":\"Alvin.Qu\",\"gender\":\"M\",\"birthday\":\"1981-03-17\"}";

    /**
     * 测试将 {@link Person} 类型对象序列化为 JSON 字符串
     *
     * <p>
     * 注意 {@link Person} 类型上标记的
     * {@link com.fasterxml.jackson.annotation.JsonIgnoreProperties @JsonIgnoreProperties} 注解,
     * 所以指定的对象属性不包含在序列化结果中
     * </p>
     */
    @Test
    void toJson_shouldEncodeObjectToJson() throws Exception {
        var mapper = new ObjectMapper();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = mapper.writeValueAsString(OBJECT);
        then(json).isEqualTo(JSON);
    }

    /**
     * 测试将 JSON 字符串反序列化为 {@link Person} 类型对象
     *
     * <p>
     * 注意 {@link Person#Person(Long, String, String, LocalDate)} 构造器上标识的
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解, 用来将 JSON
     * 字符串的指定属性反序列化为对象
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToObject() throws Exception {
        var mapper = new ObjectMapper();

        // 将 JSON 字符串反序列化为对象, 确认反序列化结果符合预期
        var obj = mapper.readValue(JSON, Person.class);
        then(obj).isEqualTo(OBJECT);
    }
}
