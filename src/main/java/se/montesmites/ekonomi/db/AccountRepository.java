package se.montesmites.ekonomi.db;

import java.time.Year;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

  Set<AccountEntity> findAllByFiscalYearCalendarYear(Year calendarYear);
}
