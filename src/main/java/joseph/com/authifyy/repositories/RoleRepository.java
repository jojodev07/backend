package joseph.com.authifyy.repositories;

import joseph.com.authifyy.entities.RoleEntity;
import joseph.com.authifyy.entities.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    Optional<RoleEntity> findByName(RoleType roleType);
}
