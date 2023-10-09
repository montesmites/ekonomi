package se.montesmites.ekonomi.endpoint.event;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.db.EntryRepository;
import se.montesmites.ekonomi.db.EventData;
import se.montesmites.ekonomi.db.EventRepository;
import se.montesmites.ekonomi.db.model.EntryDataAndAccountQualifierAndName;

@Service
@AllArgsConstructor
public class EventViewEndpoint {

  private final EventRepository eventRepository;
  private final EntryRepository entryRepository;

  public Optional<EventData> findByEventId(Long eventId) {
    return eventRepository
        .findById(eventId)
        .map(
            event ->
                new EventData(
                    event.getEventId(),
                    event.getFiscalYear().getFiscalYearId(),
                    event.getEventNo(),
                    event.getDate(),
                    event.getDescription()));
  }

  public List<EntryDataAndAccountQualifierAndName> findEntriesByEventId(Long eventId) {
    return this.entryRepository.findEntriesWithAccountsBy(eventId);
  }
}
