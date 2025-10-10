package alvin.study.misc.jackson.decode;

import java.util.List;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.json.JsonMapper;

/**
 * 将 JSON 字符串生成为对象的解码器类
 */
public class Decoder {
    // 构建 Jackson JSON 转换对象
    private final ObjectMapper mapper;

    /**
     * 构造器, 构造解码对象
     *
     * @param unwrapRootValue 是否处理根属性
     */
    public Decoder(boolean unwrapRootValue) {
        this.mapper = buildObjectMapper(unwrapRootValue);
    }

    /**
     * 构建 Jackson {@link ObjectMapper} 对象
     *
     * @param unwrapRootValue JSON 中是否具备根属性
     * @return {@link ObjectMapper} 对象
     */
    private static ObjectMapper buildObjectMapper(boolean unwrapRootValue) {
        var builder = JsonMapper
                .builder()
                .addModules(
                // new JavaTimeModule(),
                // new ParameterNamesModule(Mode.PROPERTIES)
                )
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        // 允许注释 (非标准)
        // .configure(Feature.ALLOW_COMMENTS, true)
        // 允许没有引号的字段名 (非标准)
        // .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        // 允许单引号 (非标准)
        // .configure(Feature.ALLOW_SINGLE_QUOTES, true)
        ;

        if (unwrapRootValue) {
            // 支持 @JsonRootName 设置的根属性名
            builder = builder.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        }

        return builder.build();
    }

    /**
     * 通过一个 JSON 字符串反序列化 POJO 对象
     *
     * @param <T>       返回的 POJO 对象类型
     * @param json      JSON 字符串
     * @param valueType POJO 对象类型
     * @return POJO 对象实例
     */
    public <T> T fromJson(String json, Class<T> valueType) throws JsonNodeException {
        return mapper.readValue(json, valueType);
    }

    /**
     * 通过一个 JSON 字符串反序列化 POJO 对象
     *
     * @param <T>     返回的 POJO 对象类型
     * @param json    JSON 字符串
     * @param typeRef POJO 对象类型
     * @return POJO 对象实例
     */
    public <T> T fromJson(String json, TypeReference<T> typeRef) throws JsonNodeException {
        return mapper.readValue(json, typeRef);
    }

    /**
     * 通过一个 JSON 字符串反序列化 POJO 对象
     *
     * @param <T>      返回的 POJO 对象类型
     * @param json     JSON 字符串
     * @param javaType POJO 对象类型
     * @return POJO 对象实例
     */
    public <T> T fromJson(String json, JavaType javaType) throws JsonNodeException {
        return mapper.readValue(json, javaType);
    }

    /**
     * 创建一个 {@link List} 类型的 {@link JavaType} 对象
     *
     * @param elementType {@link List} 集合的元素类型
     * @return {@link List} 类型的 {@link JavaType} 对象
     */
    public JavaType createListJavaType(Class<?> elementType) {
        return mapper.getTypeFactory().constructCollectionType(List.class, elementType);
    }
}
