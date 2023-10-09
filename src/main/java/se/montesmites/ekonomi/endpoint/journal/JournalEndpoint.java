package se.montesmites.ekonomi.endpoint.journal;

import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.db.EntryData;
import se.montesmites.ekonomi.db.EntryRepository;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.EventRepository;
import se.montesmites.ekonomi.db.FiscalYearData;
import se.montesmites.ekonomi.db.model.Amount;

@Service
@AllArgsConstructor
public class JournalEndpoint {

  private final EventRepository eventRepository;
  private final EntryRepository entryRepository;

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

  public List<EntryData> findEntriesByEventId(Long eventId) {
    return this.entryRepository.findByEventEventId(eventId, Sort.by("account.name", "rad")).stream()
        .map(
            entry ->
                new EntryData(
                    entry.getEntryId(),
                    entry.getEvent().getEventId(),
                    entry.getRowNo(),
                    entry.getAccount().accountId(),
                    new Amount(entry.getAmount())))
        .toList();
  }
}
