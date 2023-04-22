package se.montesmites.ekonomi.service;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.jpa.model.Ver;
import se.montesmites.ekonomi.jpa.repository.VerRepository;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.model.Year;

@Service
@AllArgsConstructor
public class JournalEndpoint {

  private final VerRepository verRepository;

  public int countEventsByFiscalYear(Year year) {
    return this.verRepository.countByBokfaarId(year.yearId().id());
  }

  public Stream<Event> findPageOfEventsByFiscalYear(Year year, Pageable pageable) {
    return this.verRepository.findAllByBokfaarId(year.yearId().id(), pageable).stream()
        .map(Ver::toEvent);
  }
}
