package joseph.com.authifyy.repositories;

import joseph.com.authifyy.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

}
