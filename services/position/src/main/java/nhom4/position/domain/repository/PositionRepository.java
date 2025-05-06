package nhom4.position.domain.repository;

import org.springframework.data.repository.CrudRepository;
import nhom4.position.domain.core.vo.Position;

public interface PositionRepository extends CrudRepository<Position, Integer> {
}
