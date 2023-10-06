package se.montesmites.ekonomi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.db.FiscalYearData;
import se.montesmites.ekonomi.db.FiscalYearRepository;

@Service
@RequiredArgsConstructor
public class FiscalYearService {

  private final FiscalYearRepository fiscalYearRepository;

  public List<FiscalYearData> findAll() {
    return this.fiscalYearRepository.findAll().stream()
        .map(year -> new FiscalYearData(year.getFiscalYearId(), year.getCalendarYear()))
        .toList();
  }
}
