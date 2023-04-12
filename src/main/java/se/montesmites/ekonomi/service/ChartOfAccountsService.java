package se.montesmites.ekonomi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.jpa.model.Konto;
import se.montesmites.ekonomi.jpa.repository.KontoRepository;
import se.montesmites.ekonomi.model.Account;

@Service
@RequiredArgsConstructor
public class ChartOfAccountsService {

  private final KontoRepository kontoRepository;

  public List<Account> findAll() {
    return this.kontoRepository.findAll().stream().map(Konto::toAccount).toList();
  }
}
