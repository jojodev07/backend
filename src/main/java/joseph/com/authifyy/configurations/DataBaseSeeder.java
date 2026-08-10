package joseph.com.authifyy.configurations;

import joseph.com.authifyy.entities.RoleEntity;
import joseph.com.authifyy.entities.RoleType;
import joseph.com.authifyy.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataBaseSeeder implements CommandLineRunner {

    public final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
            RoleEntity userRole = new RoleEntity();
            userRole.setName(RoleType.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
            RoleEntity userRole = new RoleEntity();
            userRole.setName(RoleType.ROLE_ADMIN);
            roleRepository.save(userRole);
        }
    }
}
