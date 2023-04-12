package se.montesmites.ekonomi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.jpa.model.Bokfaar;
import se.montesmites.ekonomi.jpa.repository.BokfaarRepository;
import se.montesmites.ekonomi.model.Year;

@Service
@RequiredArgsConstructor
public class FiscalYearService {

  private final BokfaarRepository bokfaarRepository;

  public List<Year> findAll() {
    return this.bokfaarRepository.findAll().stream().map(Bokfaar::toYear).toList();
  }
}
