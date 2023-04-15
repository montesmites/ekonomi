package se.montesmites.ekonomi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.jpa.model.Konto;
import se.montesmites.ekonomi.jpa.repository.KontoRepository;
import se.montesmites.ekonomi.model.Account;
import se.montesmites.ekonomi.model.Year;

@Service
@RequiredArgsConstructor
public class ChartOfAccountsService {

  private final KontoRepository kontoRepository;

  public List<Account> findAllByFiscalYear(Year year) {
    return this.kontoRepository.findAllByBokfaarId(year.yearId().id()).stream()
        .map(Konto::toAccount)
        .toList();
  }
}
