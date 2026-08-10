package joseph.com.authifyy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuthifyyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthifyyApplication.class, args);
    }

}
