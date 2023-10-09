package se.montesmites.ekonomi.endpoint.journal;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.EventRepository;
import se.montesmites.ekonomi.db.FiscalYearData;

@Service
@AllArgsConstructor
public class JournalEndpoint {

  private final EventRepository eventRepository;

  public int countEventsByFiscalYear(FiscalYearData fiscalYear) {
    return this.eventRepository.countByFiscalYearCalendarYear(fiscalYear.calendarYear());
  }

  public Stream<EventData> findPageOfEventsByFiscalYear(
      FiscalYearData fiscalYear, Pageable pageable) {
    return this.eventRepository
        .findAllByFiscalYearCalendarYear(fiscalYear.calendarYear(), pageable)
        .stream()
        .map(
            event ->
                new EventData(
                    event.getEventId(),
                    event.getFiscalYear().getFiscalYearId(),
                    event.getEventNo(),
                    event.getDate(),
                    event.getDescription()));
  }
}
