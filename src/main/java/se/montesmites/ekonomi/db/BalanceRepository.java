package se.montesmites.ekonomi.db;

import java.time.Year;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {

  List<BalanceEntity> findAllByAccountFiscalYearCalendarYear(Year calendarYear);
}
