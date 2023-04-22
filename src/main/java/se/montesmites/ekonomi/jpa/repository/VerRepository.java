package se.montesmites.ekonomi.jpa.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.jpa.model.VerId;

@Repository
public interface VerRepository extends JpaRepository<Ver, VerId> {

  int countByBokfaarId(String bokfaarId);

  List<Ver> findAllByBokfaarId(String bokfaarId, Pageable pageable);
}
