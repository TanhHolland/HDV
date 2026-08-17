package nhom4.uc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nhom4.uc.domain.root.Poi;

public interface PoiRepository extends JpaRepository<Poi, Integer> {
}
