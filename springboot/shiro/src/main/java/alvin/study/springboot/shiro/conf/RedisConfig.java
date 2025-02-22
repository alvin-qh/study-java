package alvin.study.springboot.shiro.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 配置 Redis
 *
 * <p>
 * {@link RedisTemplate} 对象需要配置: 1. Key 的序列化器对象; 2. Value 的序列化器对象
 * </p>
 *
 * <p>
 * 本例中 Key 为字符串类型, 使用 {@link StringRedisSerializer} 对象; 而 Value 为 JSON 字符串,
 * 使用 {@link GenericJackson2JsonRedisSerializer} 对象
 * </p>
 *
 * <p>
 * 一般情况下, Jackson 在序列化 JSON 时不会包含类型信息, 这里需要通过
 * {@link ObjectMapper#activateDefaultTyping(com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator)
 * ObjectMapper.activateDefaultTyping(PolymorphicTypeValidator)} 方法让序列化后的 JSON
 * 带有类型信息, 并反序列化为正确类型的对象
 * </p>
 */
@Configuration("core/redis")
public class RedisConfig {
    private static RedisTemplate<String, Object> getStringObjectRedisTemplate(
            LettuceConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            StringRedisSerializer stringSerializer) {

        var jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 实例化 RedisTemplate 对象
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        // 设置 Key 的序列化器
        template.setKeySerializer(stringSerializer);
        // 设置 Value 的序列化器
        template.setValueSerializer(jacksonSerializer);

        // 设置 Key 的序列化器
        template.setHashKeySerializer(stringSerializer);
        // 设置 Value 的序列化器
        template.setHashValueSerializer(jacksonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 产生一个以字符串为 Key 的 {@link RedisTemplate} 对象
     *
     * @param connectionFactory Redis 连接工厂对象
     * @param objectMapper      用于 JSON 序列化的对象
     * @return {@link RedisTemplate} 对象
     */
    @Bean
    RedisTemplate<String, Object> redisTemplate(
            LettuceConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // 克隆一个新的 ObjectMapper 用于 redis 序列化操作
        objectMapper = objectMapper.copy();
        // 设置 JSON 序列化时对源对象内容的可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 在 JSON 中包含源对象类型信息
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // Redis Key 存储序列化对象
        var stringSerializer = new StringRedisSerializer();

        // Redis Value 存储序列化对象
        return getStringObjectRedisTemplate(connectionFactory, objectMapper, stringSerializer);
    }
}
