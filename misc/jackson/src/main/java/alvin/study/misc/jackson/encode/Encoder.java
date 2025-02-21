package alvin.study.misc.jackson.encode;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import alvin.study.misc.jackson.pojo.User;

/**
 * 将对象转为 JSON 的编码器类
 */
public final class Encoder {
    // 构建 Jackson JSON 转换对象
    private final ObjectMapper mapper;

    // 定义视图标识类型
    private final Class<?>[] views;

    /**
     * 构造器, 初始化 {@link ObjectMapper} 对象
     *
     * @param views 视图类型对象, 设置序列化时参照的视图设置, 详细的视图设置, 参考 {@link User User}
     *              类型
     */
    public Encoder(boolean wrapRootValue, Class<?>... views) {
        this.mapper = buildObjectMapper(wrapRootValue);
        this.views = views;
    }

    /**
     * 构建 Jackson {@link ObjectMapper} 对象
     *
     * @param wrapRootValue 是否在 JSON 中加入根属性名
     * @return {@link ObjectMapper} 对象
     */
    private static ObjectMapper buildObjectMapper(boolean wrapRootValue) {
        var builder = JsonMapper.builder()
                .addModules(
                    // 启用 Java 时间日期模块
                    new JavaTimeModule(),
                    // 启用 JDK 8 模块
                    new Jdk8Module())
                // 日期时间以字符串而不是 timestamp 输出
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 空类型不抛出错误
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 遇到未知属性不抛出错误
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 重写枚举的 toString 方法, 针对于枚举中不包含标记为 @JsonValue 注解字段的情况
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                // 允许强制转义非 ASCII 字符, 将非 ASCII 字符转为 UNICODE 表示格式
                // .enable(JsonWriteFeature.ESCAPE_NON_ASCII)
                // 是否允许通过 @JsonView 注解进行不同的序列化
                .enable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        if (wrapRootValue) {
            // 将内容包裹为一个 JSON 属性, 通过 @JsonRootName 指定属性名
            builder = builder.enable(SerializationFeature.WRAP_ROOT_VALUE);
        }

        return builder.build()
                // JSON 中不包含 null 值
                .setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串
     * @see ObjectMapper#writeValueAsBytes(Object)
     */
    public String toJson(Object obj) throws JsonProcessingException {
        ObjectWriter writer = null;
        if (this.views != null) {
            // 如果设置了 View, 则根据 view 类型创建 writer
            for (var v : views) {
                writer = mapper.writerWithView(v); // 创建一个设置了 view 的 writer 对象
            }
        }
        if (writer != null) {
            // 如果有 writer, 则通过 writer 产生 JSON
            return writer.writeValueAsString(obj);
        }

        // 通过 mapper 产生 JSON
        return mapper.writeValueAsString(obj);
    }

    /**
     * 添加过滤器
     *
     * <p>
     * 本例中使用了
     * {@link com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter}
     * 过滤器, 其作用简单对指定的 JSON 字段进行过滤操作, 包括:
     * <ul>
     * <li>{@link com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#serializeAllExcept},
     * 处理所有字段, 仅对指定字段进行过滤</li>
     * <li>{@link com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter#filterOutAllExcept},
     * 过滤掉所有字段, 仅对指定字段进行保留</li>
     * </ul>
     * </p>
     *
     * @param filterName    过滤器名称, 每个过滤器都必须有一个唯一名称, 在 {@code @JsonFilter} 注解时候要使用
     * @param excludeFields 要通过此过滤器排除掉的字段
     */
    public void addFilter(String filterName, String... excludeFields) {
        // 产生一个基本过滤器 Provider 对象
        var provider = new SimpleFilterProvider().addFilter(
            filterName,
            SimpleBeanPropertyFilter.serializeAllExcept(Set.of(excludeFields)) // 添加一个过滤器
        );

        mapper.setFilterProvider(provider);
    }
}
