package se.montesmites.ekonomi.db;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<EntryEntity, Long> {

  List<EntryEntity> findByEventEventId(Long eventId, Sort sort);
}
