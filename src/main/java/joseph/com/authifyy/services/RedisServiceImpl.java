package joseph.com.authifyy.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

//technically not a service!
@Component
@AllArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIXPROMPT = "activation:";
    private static final String PREFIXCONGRATULATE = "activationFinish:";


    @Override
    public void save(String token, String prefix, String value, Duration ttl) {
        redisTemplate.opsForValue().set(prefix + token, value, ttl);
    }


    @Override
    public Optional<String> consumePrompt(String token) {
        String value = redisTemplate.opsForValue().getAndDelete(PREFIXPROMPT + token);
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<String> consumeCongratulate(String token) {
        String value = redisTemplate.opsForValue().getAndDelete(PREFIXCONGRATULATE + token);
        return Optional.ofNullable(value);
    }


}
