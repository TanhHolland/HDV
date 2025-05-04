package qbike.uc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qbike.uc.domain.root.Poi;

public interface PoiRepository extends JpaRepository<Poi, Integer> {
}
