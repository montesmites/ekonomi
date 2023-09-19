package se.montesmites.ekonomi.jpa.migration;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.montesmites.ekonomi.db.AccountEntity;
import se.montesmites.ekonomi.db.AccountRepository;
import se.montesmites.ekonomi.db.BalanceEntity;
import se.montesmites.ekonomi.db.BalanceRepository;
import se.montesmites.ekonomi.db.EntryEntity;
import se.montesmites.ekonomi.db.EntryRepository;
import se.montesmites.ekonomi.db.EventEntity;
import se.montesmites.ekonomi.db.EventRepository;
import se.montesmites.ekonomi.db.FiscalYearEntity;
import se.montesmites.ekonomi.db.FiscalYearRepository;
import se.montesmites.ekonomi.model.AccountStatus;
import se.montesmites.ekonomi.model.Entry;
import se.montesmites.ekonomi.model.Event;
import se.montesmites.ekonomi.sie.file.SieToOrganizationConverter;

@SpringBootTest
public class LegacyDatabaseMigratorTest {

  private static final Path PATH =
      Paths.get("C:/ProgramData/SPCS/SPCS Administration/Företag/nikka/sie");

  private static final SieToOrganizationConverter CONVERTER = SieToOrganizationConverter.of();

  @Autowired private AccountRepository accountRepository;
  @Autowired private BalanceRepository balanceRepository;
  @Autowired private FiscalYearRepository fiscalYearRepository;
  @Autowired private EntryRepository entryRepository;
  @Autowired private EventRepository eventRepository;

  @ParameterizedTest
  @ValueSource(ints = {2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022})
  @Disabled
  void doImportFromSie4(int year) {
    var file = PATH.resolve(year + "_sie4_transaktioner-och-balanser.SE");

    var sie4 = CONVERTER.convert(file);

    var fiscalYear = fiscalYearRepository.save(new FiscalYearEntity(null, Year.of(year)));

    var accounts =
        sie4.streamAccounts()
            .sorted(comparing(account -> account.accountId().id()))
            .map(
                account ->
                    new AccountWithLegacyAccountId(
                        new LegacyAccountId(account.accountId().id()),
                        accountRepository.save(
                            new AccountEntity(
                                null,
                                fiscalYear,
                                account.accountId().id(),
                                account.description(),
                                account.accountStatus() == AccountStatus.OPEN))))
            .collect(
                toMap(
                    AccountWithLegacyAccountId::legacyAccountId,
                    AccountWithLegacyAccountId::account));

    sie4.streamBalances()
        .sorted(comparing(balance -> balance.accountId().id()))
        .map(
            balance ->
                balanceRepository.save(
                    new BalanceEntity(
                        null,
                        accounts.get(new LegacyAccountId(balance.accountId().id())),
                        longToBigDecimal(balance.balance().amount()))))
        .forEach(__ -> {});

    var events =
        sie4.streamEvents()
            .sorted(
                comparing((Event event) -> event.eventId().id())
                    .thenComparing((Event event) -> event.eventId().id()))
            .map(
                event ->
                    eventRepository.save(
                        new EventEntity(
                            null,
                            fiscalYear,
                            event.eventId().id(),
                            event.date(),
                            event.description())))
            .collect(toMap(EventEntity::getEventNo, event -> event));

    sie4.streamEntries()
        .sorted(comparing((Entry entry) -> entry.eventId().id()).thenComparing(Entry::rowNo))
        .map(
            entry ->
                entryRepository.save(
                    new EntryEntity(
                        null,
                        events.get(entry.eventId().id()),
                        entry.rowNo(),
                        accounts.get(new LegacyAccountId(entry.accountId().id())),
                        longToBigDecimal(entry.amount().amount()))))
        .forEach(__ -> {});
  }

  @ParameterizedTest
  @ValueSource(ints = {2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022})
  @Disabled
  void importSie4BalancesOnly(int year) {
    var file = PATH.resolve(year + "_sie4_transaktioner-och-balanser.SE");

    var sie4 = CONVERTER.convert(file);

    var accounts =
        accountRepository.findAllByFiscalYearCalendarYear(Year.of(year)).stream()
            .collect(toMap(AccountEntity::qualifier, account -> account));

    sie4.streamBalances()
        .sorted(comparing(balance -> balance.accountId().id()))
        .map(
            balance ->
                balanceRepository.save(
                    new BalanceEntity(
                        null,
                        accounts.get(balance.accountId().id()),
                        longToBigDecimal(balance.balance().amount()))))
        .forEach(__ -> {});
  }

  private BigDecimal longToBigDecimal(Long amount) {
    return amount == null ? null : BigDecimal.valueOf(amount, 2);
  }

  private record LegacyAccountId(String qualifier) {}

  private record AccountWithLegacyAccountId(
      LegacyAccountId legacyAccountId, AccountEntity account) {}

  private record EventId(String bokfaarId, Integer eventNo) {}

  private record EventWithEventId(EventId eventId, EventEntity event) {}
}
