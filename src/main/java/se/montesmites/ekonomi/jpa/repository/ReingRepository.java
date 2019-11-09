package se.montesmites.ekonomi.jpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.montesmites.ekonomi.jpa.model.Reing;

@Repository
public interface ReingRepository extends JpaRepository<Reing, Integer> {

  List<Reing> findByKontoIdIsNotNullAndIngaendeBalansIsNotNull();
}
