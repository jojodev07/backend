package joseph.com.authifyy.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfigs {

    @Bean
    public ModelMapper GetModelMapper() {
        return new ModelMapper();
    }
}
