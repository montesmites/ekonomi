package se.montesmites.ekonomi.service;

import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.montesmites.ekonomi.db.AccountRepository;
import se.montesmites.ekonomi.db.AccountWithQualifierAndName;

@Service
@RequiredArgsConstructor
public class ChartOfAccountsService {

  private final AccountRepository accountRepository;

  public List<AccountWithQualifierAndName> findAllByFiscalYear(Year year) {
    return this.accountRepository.findAllByFiscalYearCalendarYear(year).stream()
        .map(account -> new AccountWithQualifierAndName(account.qualifier(), account.name()))
        .toList();
  }
}
