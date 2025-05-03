package qbike.position.domain.repository;

import org.springframework.data.repository.CrudRepository;
import qbike.position.domain.core.vo.Position;

public interface PositionRepository extends CrudRepository<Position, Integer> {
}
