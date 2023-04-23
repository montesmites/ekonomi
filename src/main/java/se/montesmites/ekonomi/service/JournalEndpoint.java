package se.montesmites.ekonomi.service;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.jpa.model.Verrad;
import se.montesmites.ekonomi.jpa.repository.VerRepository;
import se.montesmites.ekonomi.jpa.repository.VerradRepository;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.EventId;
import se.montesmites.ekonomi.model.Year;

@Service
@AllArgsConstructor
public class JournalEndpoint {

  private final VerRepository verRepository;
  private final VerradRepository verradRepository;

  public int countEventsByFiscalYear(Year year) {
    return this.verRepository.countByBokfaarId(year.yearId().id());
  }

  public Stream<Event> findPageOfEventsByFiscalYear(Year year, Pageable pageable) {
    return this.verRepository.findAllByBokfaarId(year.yearId().id(), pageable).stream()
        .map(Ver::toEvent);
  }

  public int countVerradByEventId(EventId eventId) {
    return this.verradRepository.countByBokfaarIdAndVerserieAndVernr(
        eventId.yearId().id(), eventId.series().series(), eventId.id());
  }

  public Stream<Entry> findVerradByEventId(EventId eventId) {
    return this.verradRepository
        .findByBokfaarIdAndVerserieAndVernr(
            eventId.yearId().id(),
            eventId.series().series(),
            eventId.id(),
            Sort.by("kontoId", "rad"))
        .stream()
        .map(Verrad::toEntry);
  }
}
