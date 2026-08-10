package joseph.com.authifyy.controllers;

import joseph.com.authifyy.services.RedisService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/redis")
@AllArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/verify-opaque")
    public ResponseEntity<?> verifyOpaque(@RequestParam(name = "token") String token) {
        return ResponseEntity.status(HttpStatus.OK).body(
                redisService.consumePrompt(token)
        );
    }


}
