package alvin.study.springboot.security.conf;

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
 */
@Configuration("core/redis")
public class RedisConfig {
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
        // 复制一个新的 ObjectMapper 用于 redis 序列化操作, 并设置 json 中存储对象元数据
        objectMapper = objectMapper.copy();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // Redis Key 存储序列化对象
        var stringSerializer = new StringRedisSerializer();

        // Redis Value 存储序列化对象
        var jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jacksonSerializer);

        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
