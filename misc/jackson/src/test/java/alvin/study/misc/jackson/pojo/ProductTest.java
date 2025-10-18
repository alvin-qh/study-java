package alvin.study.misc.jackson.pojo;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import alvin.study.misc.jackson.decode.Decoder;

/**
 * 测试 {@link Product} 类型的 JSON 序列化以及反序列化操作
 *
 * <p>
 * 本测试演示通过 {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator}
 * 注解来完成 JSON 到对象的反序列化
 * </p>
 *
 * <p>
 * 注意: {@link Product} 类标记了
 * {@link com.fasterxml.jackson.annotation.JsonRootName @JsonRootName} 注解,
 * 所以会多出一个根属性 {@code product}
 * </p>
 */
class ProductTest {
    // 要序列化的对象
    private static final Product OBJECT = new Product(
        1L,
        "Computer",
        "S001",
        Instant.parse("2022-03-17T13:20:00Z"));

    // 期待的序列化结果, 注意 root 属性
    private static final String JSON
        = "{\"product\":{\"id\":1,\"name\":\"Computer\",\"serialNo\":\"S001\",\"date\":\"2022-03-17T13:20:00Z\"}}";

    /**
     * 测试在序列化时指定结果 JSON 的根属性名称
     *
     * <p>
     * {@link Product} 类型具备 {@link com.fasterxml.jackson.annotation.JsonRootName @JsonRootName} 注解,
     * 该注解表示 {@link Product} 对象序列化后的的内容都会包含在结果 JSON 的指定属性下, 需要在创建 {@link JsonMapper} 对象时, 通过
     * {@link com.fasterxml.jackson.databind.SerializationFeature#WRAP_ROOT_VALUE
     * SerializationFeature.WRAP_ROOT_VALUE} 配置项, 指定要在最外层包裹一个根元素
     * </p>
     */
    @Test
    void toJson_shouldEncodeProductObjectToJson() throws Exception {
        var mapper = JsonMapper.builder()
                // 表示在序列化时, 将 Product 对象整体包裹为 JSON 的一个属性值, 该属性值的名称通过 @JsonRootName 注解指定
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();

        // 将对象序列化为 JSON 字符串, 确认序列化结果符合预期
        var json = mapper.writeValueAsString(OBJECT);
        then(json).isEqualTo(JSON);
    }

    /**
     * 测试 {@link Decoder#fromJson(String, Class)} 方法
     *
     * <p>
     * 通过 {@link Product#Product(Long, String, String, Instant)} 构造器上的
     * {@link com.fasterxml.jackson.annotation.JsonCreator @JsonCreator} 注解指定如何通过 JSON 反序列化为对象
     * </p>
     *
     * <p>
     * 要启用 {@link com.fasterxml.jackson.annotation.JsonRootName @JsonRootName} 注解, 需要在调用
     * {@link Decoder#Decoder(boolean)} 构造器时, 传递参数 {@code true}, 表示启用
     * {@link com.fasterxml.jackson.databind.DeserializationFeature#UNWRAP_ROOT_VALUE
     * DeserializationFeature.UNWRAP_ROOT_VALUE} 配置
     * </p>
     */
    @Test
    void fromJson_shouldDecodeJsonToProductObject() throws Exception {
        var mapper = JsonMapper.builder()
                // 表示在反序列化时, 将 JSON 的指定属性值反序列化为 Product 对象, 该属性值的名称通过 @JsonRootName 注解指定
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .build();

        // 将 JSON 字符串反序列化为对象, 确认结果符合预期
        var obj = mapper.readValue(JSON, Product.class);
        then(obj).isEqualTo(OBJECT);
    }
}
