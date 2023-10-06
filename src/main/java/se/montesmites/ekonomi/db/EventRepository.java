package se.montesmites.ekonomi.db;

import java.time.Year;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

  int countByFiscalYearCalendarYear(Year calendarYear);

  List<EventEntity> findAllByFiscalYearCalendarYear(Year calendarYear, Pageable pageable);
}
