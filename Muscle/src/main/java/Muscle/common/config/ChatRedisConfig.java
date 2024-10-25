
package Muscle.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
public class ChatRedisConfig {

    @Value("${spring.redis.host}")
    private String chatRedisHost;

    @Value("${spring.redis.port}")
    private int chatRedisPort;

    @Bean
    public RedisConnectionFactory chatRedisConnectionFactory() {
        return new LettuceConnectionFactory(chatRedisHost, chatRedisPort);
    }

    @Bean
    public RedisTemplate<String, Object> chatRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Custom ObjectMapper without default typing
        ObjectMapper objectMapper = new ObjectMapper();
        // Disable default typing to avoid type information being included
        objectMapper.deactivateDefaultTyping();

        // Use GenericJackson2JsonRedisSerializer with custom ObjectMapper
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        template.afterPropertiesSet();
        return template;
    }
}
