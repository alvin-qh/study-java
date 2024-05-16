package alvin.study.misc.jackson.pojo;

import alvin.study.misc.jackson.decode.Decoder;
import alvin.study.misc.jackson.encode.Encoder;
import alvin.study.misc.jackson.pojo.common.SimpleDate;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link Staff} 类型的 JSON 序列化和反序列化
 *
 * <p>
 * 在 {@link Staff#name} 属性上标记了 {@link com.fasterxml.jackson.annotation.JsonProperty @JsonProperty} 注解,
 * 用于指定对象属性和 JSON 字段的映射名称
 * </p>
 *
 * <p>
 * 在 {@link Staff#valid} 属性上标记了 {@link com.fasterxml.jackson.annotation.JsonIgnore @JsonIgnore} 注解,
 * 表示该对象类型不参与 JSON 序列化和反序列化
 * </p>
 *
 * <p>
 * 另外, {@link Staff#Staff()} 构造器用于 JSON 反序列化时, 构造对象
 * </p>
 */
class StaffTest {
    // 要序列化的对象
    private static final Staff OBJECT = new Staff(
        1L,
        "Alvin",
        "M",
        new SimpleDate(1981, 3, 17),
        true);

    // 期待的单个对象序列化结果
    private static final String SINGLE_JSON
        = "{\"id\":1,\"gender\":\"M\",\"birthday\":{\"year\":1981,\"month\":3,\"day\":17},\"staffName\":\"Alvin\"}";

    // 期待的集合对象序列化结果
    private static final String LIST_JSON
        = "[{\"id\":1,\"gender\":\"M\",\"birthday\":{\"year\":1981,\"month\":3,\"day\":17},\"staffName\":\"Alvin\"}]";

    /**
     * 测试 {@link Encoder#toJson(Object)} 方法
     *
     * <p>
     * 将单个对象序列化为 JSON 字符串
     * </p>
     */
    @Test
    void toJson_shouldEncodeProductObjectToJson() throws Exception {
        var enc = new Encoder(false);

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = enc.toJson(OBJECT);
        then(json).isEqualTo(SINGLE_JSON);
    }

    /**
     * 测试 {@link Encoder#toJson(Object)} 方法
     *
     * <p>
     * 将 {@link List} 集合对象序列化为 JSON 字符串
     * </p>
     */
    @Test
    void toJson_shouldEncodeProductListToJson() throws Exception {
        var enc = new Encoder(false);

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = enc.toJson(List.of(OBJECT));
        then(json).isEqualTo(LIST_JSON);
    }

    /**
     * 测试 {@link Decoder#fromJson(String, Class)} 方法
     *
     * <p>
     * 将 JSON 字符串转为单个对象
     * </p>
     *
     * <p>
     * 通过 {@link Staff#Staff(Long, String, String, SimpleDate, boolean)} 构造器上的
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解指定如何通过 JSON 反序列化为对象
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductObject() throws Exception {
        var dec = new Decoder(false);

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var obj = dec.fromJson(SINGLE_JSON, Staff.class);
        then(obj).isEqualTo(OBJECT);
    }

    /**
     * 测试 {@link Decoder#fromJson(String, TypeReference)} 方法
     *
     * <p>
     * 通过 {@link Staff#Staff(Long, String, String, SimpleDate, boolean)} 构造器上的
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解指定如何通过 JSON 反序列化为对象
     * </p>
     *
     * <p>
     * 本次反序列化的类型包含泛型类型, 需通过 {@link TypeReference} 类型传递泛型类型参数
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductListByTypeReferenceArgument() throws Exception {
        var dec = new Decoder(false);

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var list = dec.fromJson(LIST_JSON, new TypeReference<List<Staff>>() { });
        BDDAssertions.then(list).containsExactly(OBJECT);
    }

    /**
     * 测试 {@link Decoder#fromJson(String, TypeReference)} 方法
     *
     * <p>
     * 通过 {@link Staff#Staff(Long, String, String, SimpleDate, boolean)} 构造器上的
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解指定如何通过 JSON 反序列化为对象
     * </p>
     *
     * <p>
     * 本次反序列化的类型包含泛型类型, 需通过 {@link com.fasterxml.jackson.databind.JavaType JavaType} 类型传递泛型类型参数,
     * 参考 {@link Decoder#createListJavaType(Class)} 创建泛型集合类型
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductListByJavaTypeArgument() throws Exception {
        var dec = new Decoder(false);

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var list = dec.<List<Staff>>fromJson(LIST_JSON, dec.createListJavaType(Staff.class));
        BDDAssertions.then(list).containsExactly(OBJECT);
    }
}
