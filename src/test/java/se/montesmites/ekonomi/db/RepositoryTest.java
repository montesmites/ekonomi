package se.montesmites.ekonomi.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RepositoryTest {

  @Autowired private AccountRepository accountRepository;
  @Autowired private BalanceRepository balanceRepository;
  @Autowired private EntryRepository entryRepository;
  @Autowired private EventRepository eventRepository;
  @Autowired private FiscalYearRepository fiscalYearRepository;

  @Test
  void fiscalYear() {
    var expected = List.of(fiscalYearRepository.save(year(2023)));
    var actual = fiscalYearRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void account() {
    var expected =
        List.of(accountRepository.save(account(year(2023), "1234", "one-two-three-four")));
    var actual = accountRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void balance() {
    var expected =
        List.of(
            balanceRepository.save(
                balance(account(year(2023), "1234", "one-two-three-four"), BigDecimal.ONE)));
    var actual = balanceRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void eventTest() {
    var expected = List.of(eventRepository.save(event(year(2023), LocalDate.now(), "description")));
    var actual = eventRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void entryTest() {
    var expected =
        List.of(
            entryRepository.save(
                entry(
                    event(year(2023), LocalDate.now(), "description"),
                    account(year(2023), "1234", "one-two-three-four"),
                    BigDecimal.ONE)));
    var actual = entryRepository.findAll();

    assertEquals(expected, actual);
  }

  private FiscalYearEntity year(int year) {
    return fiscalYearRepository.save(new FiscalYearEntity(null, Year.of(year)));
  }

  private AccountEntity account(FiscalYearEntity fiscalYear, String qualifier, String name) {
    return accountRepository.save(new AccountEntity(null, fiscalYear, qualifier, name, true));
  }

  private BalanceEntity balance(AccountEntity account, BigDecimal balance) {
    return balanceRepository.save(new BalanceEntity(null, account, balance));
  }

  private EventEntity event(FiscalYearEntity year, LocalDate eventDate, String description) {
    return eventRepository.save(new EventEntity(null, year, eventDate, description));
  }

  private EntryEntity entry(EventEntity event, AccountEntity account, BigDecimal amount) {
    return entryRepository.save(new EntryEntity(null, event, account, amount));
  }
}
