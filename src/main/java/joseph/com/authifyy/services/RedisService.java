package joseph.com.authifyy.services;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {
    void save(String token, String prefix , String value, Duration ttl);
    Optional<String> consumePrompt(String token);
    Optional<String> consumeCongratulate(String token);
}
